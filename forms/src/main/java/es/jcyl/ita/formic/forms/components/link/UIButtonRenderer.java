package es.jcyl.ita.formic.forms.components.link;
/*
 * Copyright 2020  Rosa María Muñiz (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
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

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.events.Event;
import es.jcyl.ita.formic.forms.actions.events.UserEventInterceptor;
import es.jcyl.ita.formic.forms.dialog.ConfirmationDialog;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;
import es.jcyl.ita.formic.forms.view.render.AbstractRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @author Rosa María Muñiz (mungarro@itacyl.es)
 * <p>
 * Renders link component using android views
 */
public class UIButtonRenderer extends AbstractRenderer<UIButton, Widget<UIButton>> {

    @Override
    protected int getWidgetLayoutId(UIButton component) {
        return R.layout.widget_button;
    }

    @Override
    protected void composeWidget(RenderingEnv env, Widget<UIButton> widget) {
        UIButton component = widget.getComponent();

        Button addButton = widget.findViewById(R.id.btn);

        if (StringUtils.isNotBlank(component.getLabel())) {
            addButton.setText(component.getLabel());
        }
        addButton.setOnClickListener(new ButtonClickListener(widget, env));
    }

    public class ButtonClickListener implements View.OnClickListener {

        private final Widget widget;
        private final RenderingEnv env;

        public ButtonClickListener(Widget widget, RenderingEnv env) {
            this.widget = widget;
            this.env = env;
        }

        @Override
        public void onClick(View v) {
            UIButton component = (UIButton) widget.getComponent();
            if ((Boolean) ConvertUtils.convert(component.isReadonly(env.getWidgetContext()), Boolean.class)) {
                if (component.getMessageType().equalsIgnoreCase(String.valueOf(UILinkBase.MESSAGETYPE.TOAST))){
                    UserMessagesHelper.toast(env.getAndroidContext(), component.getReadonlyMessage(),
                            Toast.LENGTH_LONG);
                }else{
                    ConfirmationDialog confirmationDialog = new ConfirmationDialog(env.getAndroidContext());
                    confirmationDialog.show();

                    confirmationDialog.getConfirmationDialogText().setText(component.getReadonlyMessage());
                    confirmationDialog.getConfirmationDialogTitle().setText(StringUtils.upperCase(env.getAndroidContext().getString(R.string.information)));

                    confirmationDialog.getAcceptButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationDialog.cancel();
                        }
                    });
                    confirmationDialog.getCancelButton().setVisibility(View.GONE);
                }
            } else {
                if (component.isConfirmation(env.getWidgetContext())) {
                    ConfirmationDialog confirmationDialog = new ConfirmationDialog(env.getAndroidContext());
                    confirmationDialog.show();

                    confirmationDialog.getConfirmationDialogText().setText(component.getLabelConfirmation());
                    confirmationDialog.getConfirmationDialogTitle().setText(StringUtils.upperCase(env.getAndroidContext().getString(R.string.confirmation)));

                    confirmationDialog.getAcceptButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            execute(env, widget);
                            confirmationDialog.cancel();
                        }
                    });
                    confirmationDialog.getCancelButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationDialog.cancel();
                        }
                    });
                } else {
                    execute(env, widget);
                }
            }
        }
    }

    private void execute(RenderingEnv env, Widget<UIButton> widget) {
        UserEventInterceptor interceptor = env.getUserActionInterceptor();
        if (interceptor != null) {
            Event event = new Event(Event.EventType.CLICK, widget);
            interceptor.notify(event);
        }
    }

    @Override
    protected void setupWidget(RenderingEnv env, Widget<UIButton> widget) {
        // Do nothing
    }

}
