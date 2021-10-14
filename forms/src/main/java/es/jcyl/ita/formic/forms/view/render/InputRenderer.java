package es.jcyl.ita.formic.forms.view.render;
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
 * Extends base renderer to define a common creation flow form UIInput components.
 * I, represents the AndroidView used as base user input, the component attached to InputFieldView
 * that will be used to get and set data from/to the view.
 * <p/>
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import java.io.File;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.renderer.MessageHelper;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;

import static android.view.View.inflate;


public abstract class InputRenderer<C extends UIInputComponent, I extends View>
        extends AbstractRenderer<C, InputWidget<C, I>> {

    @Override
    protected void composeWidget(RenderingEnv env, InputWidget<C, I> widget) {
        C component = widget.getComponent();
        // find and attach label and input
        widget.setConverter(component.getConverter());
        widget.setInputId(component.getId());

        // find label and setup
        TextView fieldLabel = ViewHelper.findViewAndSetId(widget, getLabelViewId(),
                TextView.class);
        if (fieldLabel != null && StringUtils.isNotEmpty(component.getLabel())) {
            setLabel(fieldLabel, component);
        }

        // get input view and set Tag and Value
        I inputView = (I) ViewHelper.findViewAndSetId(widget, getInputViewId());
        if (inputView == null) {
            Resources res = Config.getInstance().getResources();
            DevConsole.error(String.format("There's an error in the '%s' class, the method  " +
                            "getInputViewId() must return an existing component in the widget " +
                            "layout. Make sure there's a View with the id [%s] in the file [%s].",
                    this.getClass().getName(), res.getResourceName(getInputViewId()),
                    res.getResourceName(getWidgetLayoutId(component))));
        }
        widget.setInputView(inputView);
        setInputView(env, widget);

        //Info button
        setInfoButton(env, widget);
        setVisibilityInfoButton(env, widget);
        setOnClickListenerInfoButton(env, widget);

        //Clear button
        setResetButton(env, widget);
        setVisibilityResetButton(env, widget);

        // implement specific component rendering
        composeInputView(env, widget);

        //Visibilty Button Layout
        setVisibiltyButtonLayout(widget, StringUtils.isNotBlank(component.getLabel()), component.getResetButton(), component.getInfoButton());

        // set value and error messages
        setValueInView(env, widget);
        setMessages(env, widget);

        if ((fieldLabel == null || StringUtils.isEmpty(component.getLabel())) && MessageHelper.getMessage(env, component)==null) {
            fieldLabel.setVisibility(View.GONE);
        }

    }

    protected void setInfoButton(RenderingEnv env, InputWidget<C, I> widget){
        I infoButton = (I) ViewHelper.findViewAndSetId(widget, getInfoButtonId());
        C component = widget.getComponent();
        component.setInfoButton((ImageView) infoButton);
    }

    protected void setOnClickListenerInfoButton(RenderingEnv env, InputWidget<C, I> widget){
        C component = widget.getComponent();
        ImageView infoButton = component.getInfoButton();
        String hint = component.getHint();

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(env.getAndroidContext(), R.style.DialogStyle);
                final View view = inflate(env.getAndroidContext(), R.layout.info_dialog, null);
                TextView titleView = view.findViewById(R.id.info);

                titleView.setText(Html.fromHtml(hint, new Html.ImageGetter() {

                    @Override
                    public Drawable getDrawable(String source) {

                        /*int resourceId = env.getAndroidContext().getResources().getIdentifier(source, "drawable", env.getAndroidContext().getApplicationContext().getPackageName());
                        Drawable drawable = env.getAndroidContext().getResources().getDrawable(resourceId);*/

                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        File file = new File(path + File.separator + source);

                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                        Drawable drawable = new BitmapDrawable(env.getAndroidContext().getResources(), bitmap);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

                        return drawable;
                    }
                }, null));

                builder.setCustomTitle(view)
                        .setPositiveButton("OK", null);
                Dialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    protected void setVisibilityInfoButton(RenderingEnv env, InputWidget<C, I> widget){
        C component = widget.getComponent();
        String hint = component.getHint();
        if (StringUtils.isBlank(hint)){
            component.getInfoButton().setVisibility(View.INVISIBLE);
        }
    }

    protected void setResetButton(RenderingEnv env, InputWidget<C, I> widget){
        // set clear button
        I resetButton = (I) ViewHelper.findViewAndSetId(widget, getResetButtonId());
        C component = widget.getComponent();
        //UIInputComponent component = (UIInputComponent) widget.getComponent();
        component.setResetButton((ImageView) resetButton);
    }

    protected void setVisibilityResetButton(RenderingEnv env, InputWidget<C, I> widget){
        C component = widget.getComponent();
        if ((Boolean) ConvertUtils.convert(component.isReadonly(env.getWidgetContext()), Boolean.class)
                || !component.hasDeleteButton()) {
            component.getResetButton().setVisibility(View.GONE);
        }
    }

    protected void setValueInView(RenderingEnv env, InputWidget<C, I> widget) {
        String value = getComponentValue(env, widget.getComponent(), String.class);
        widget.getConverter().setViewValue(widget.getInputView(), value);
    }

    protected int getLabelViewId() {
        return R.id.label_view;
    }

    protected int getInputViewId() {
        return R.id.input_view;
    }

    protected int getInfoButtonId() {
        return R.id.field_layout_info;
    }

    protected int getResetButtonId() {
        return R.id.field_layout_x;
    }

    protected int getButtonsLayoutId() {
        return R.id.label_buttons_layout;
    }



    /**
     * Calculates the String to tag the view component that stores the user input
     * getInputTag
     *
     * @return
     */
    protected String getInputTag(C c) {
        return getWidgetViewTag(c) + ">input";
    }

    protected void setLabel(TextView labelView, C component) {
        labelView.setTag("label_" + component.getId());
        String labelComponent = (component.isMandatory()) ?
                component.getLabel() + " *"
                : component.getLabel();
        labelView.setText(labelComponent);
    }

    protected void setInputView(RenderingEnv env, InputWidget<C, I> widget) {
        // link inputView with baseView
        C component = widget.getComponent();
        I inputView = widget.getInputView();
        String inputTag = getInputTag(component);
        inputView.setTag(inputTag);
        inputView.setEnabled(!(Boolean) ConvertUtils.convert(widget.getComponent().isReadonly(env.getWidgetContext()), Boolean.class));
        //inputView.setFocusableInTouchMode(!(Boolean) ConvertUtils.convert(widget.getComponent().isReadonly(env.getWidgetContext()), Boolean.class));
    }

    protected void setVisibiltyButtonLayout(InputWidget<C, I> widget, boolean hasLabel, ImageView resetButton, ImageView infoButton){
        if (!isInfoButtonVisible(infoButton) && !isResetButtonVisible(resetButton) && !hasLabel){
            ViewGroup layout = (ViewGroup) ViewHelper.findViewAndSetId(widget, getButtonsLayoutId(), ViewGroup.class);
            //ViewGroup layout = (ViewGroup) resetButton.getParent();
            layout.setVisibility(View.GONE);
        }
    }

    private boolean isInfoButtonVisible(ImageView infoButton){
        return infoButton != null && infoButton.getVisibility() != View.INVISIBLE && infoButton.getVisibility() != View.GONE;
    }

    private boolean isResetButtonVisible(ImageView resetButton){
        return resetButton != null && resetButton.getVisibility() != View.INVISIBLE && resetButton.getVisibility() != View.GONE;
    }

    /************************************/
    /** Extension points **/
    /************************************/

    protected abstract void setMessages(RenderingEnv env, InputWidget<C, I> widget);

    protected abstract void composeInputView(RenderingEnv env, InputWidget<C, I> widget);

}
