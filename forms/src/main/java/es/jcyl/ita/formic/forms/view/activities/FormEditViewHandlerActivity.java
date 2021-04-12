package es.jcyl.ita.formic.forms.view.activities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JxltEngine;
import org.mini2Dx.beanutils.ConvertUtils;

import java.io.Serializable;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.formic.forms.components.link.UIParam;
import es.jcyl.ita.formic.forms.controllers.FCAction;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;

/*
 * Copyright 2020 Gustavo Río Briones (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Gustavo Río Briones (gustavo.rio@itacyl.es)
 */

public class FormEditViewHandlerActivity extends BaseFormActivity<FormEditController>
        implements FormActivity<FormEditController> {


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_form_edit_view_handler;
    }

    @Override
    protected void doRender(RenderingEnv renderingEnv) {
        // add action buttons
        ViewGroup toolBar = findViewById(R.id.form_toolbar);
        renderToolBar(toolBar);

    }

    private void renderToolBar(ViewGroup parentView) {
        if (this.formController.getActions() != null) {
            for (FCAction action : this.formController.getActions()) {
                renderActionButton(this, parentView, action);
            }
        }
    }

    protected void close() {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainController mc = MainController.getInstance();
        UserAction action = UserAction.back(this.formController);
//        action.setOrigin(formController.getId());
        mc.getActionController().doUserAction(action);
        finish();
    }

    private Button renderActionButton(Context context, ViewGroup parent, FCAction formAction) {
        Button button = new Button(context);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUserActionInterceptor interceptor = env.getUserActionInterceptor();
                if (interceptor != null) {
                    // TODO: FORMIC-202 UIButton, UILinkRenderer utilizar método desde UserActionHelper
                    String strRoute = "";
                    if (formAction.getRoute() != null) {
                        JxltEngine.Expression e = JexlFormUtils.createExpression(formAction.getRoute());
                        Object route = e.evaluate((JexlContext) env.getContext());
                        strRoute = (String) ConvertUtils.convert(route, String.class);
                    }
                    UserAction action = new UserAction(formAction.getType(), strRoute, formController);
                    action.setRegisterInHistory(formAction.isRegisterInHistory());
                    action.setForceRefresh(formAction.isForceRefresh());
                    action.setMessage(formAction.getMessage());
                    if (formAction.hasParams()) {
                        for (UIParam param : formAction.getParams()) {
                            Object value = JexlFormUtils.eval(env.getContext(), param.getValue());
                            if (value != null) {
                                action.addParam(param.getName(), (Serializable) value);
                            }
                        }
                    }
                    interceptor.doAction(action);
                }
            }
        });
        button.setText(formAction.getLabel());
        setLayoutParams(button);
        setButtonStyle(button);
        parent.addView(button);
        return button;
    }

    private void setLayoutParams(Button button) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = (float) 0.5;
        button.setLayoutParams(layoutParams);
    }

    /**
     * @param button
     * @return
     */
    private void setButtonStyle(Button button) {

        int[] attrs = new int[]{R.attr.buttonNormalColor};
        TypedArray ta = this.obtainStyledAttributes(attrs);
        int normalColor = ta.getColor(0, Color.GRAY);
        button.setBackgroundColor(normalColor);

        attrs = new int[]{R.attr.buttonHighlightColor};
        ta = this.obtainStyledAttributes(attrs);
        int highlightColor = ta.getColor(0, Color.GRAY);
        button.setHighlightColor(highlightColor);

        attrs = new int[]{R.attr.buttonTextColor};
        ta = this.obtainStyledAttributes(attrs);
        int textColor = ta.getColor(0, Color.BLACK);
        button.setTextColor(textColor);
    }


}