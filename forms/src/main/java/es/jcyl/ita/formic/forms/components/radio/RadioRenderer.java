package es.jcyl.ita.formic.forms.components.radio;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.events.Event;
import es.jcyl.ita.formic.forms.actions.events.UserEventInterceptor;
import es.jcyl.ita.formic.forms.components.StyleHolder;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.option.UIOption;
import es.jcyl.ita.formic.forms.components.util.ComponentUtils;
import es.jcyl.ita.formic.forms.view.render.InputRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.MessageHelper;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.repo.EntityMapping;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

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

public class RadioRenderer extends InputRenderer<UIRadio, RadioGroup> {
//    private static final EmptyOption EMPTY_OPTION = new EmptyOption(null, null);


    @Override
    protected void composeInputView(RenderingEnv env, InputWidget<UIRadio, RadioGroup> widget) {
        RadioGroup radioGroup = widget.getInputView();
        UIRadio component = widget.getComponent();
        radioGroup.setOrientation(component.getOrientationType());

        // create options
        UIOption[] options = component.getOptions();
        RadioButton button;
        int i = 0;

        if (ArrayUtils.isNotEmpty(options)) {
            float[] weigthts = ComponentUtils.getWeigths(component.getWeights(), options.length, component.getId(), null);
            for (UIOption option : options) {
                button = new RadioButtonWidget(env.getAndroidContext(), option);
                button.setText(option.getLabel());
                button.setId(i);

                StyleHolder<RadioButton> styleHolder = new RadioButtonStyleHolder(env.getAndroidContext());
                styleHolder.applyStyle(button);

                radioGroup.addView(button);
                setLayoutParams(weigthts, i, button, component.getOrientationType());
                i++;
            }
        }

        // set listener to handler option clicks
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                UserEventInterceptor interceptor = env.getUserActionInterceptor();
                if (interceptor != null) {
                    interceptor.notify(Event.inputChange(widget));
                }
            }
        });

        setOnClickListenerResetButton(widget);

        if (component.isReadonly(env.getWidgetContext())) {
            for (int index = 0; index < radioGroup.getChildCount(); index++) {
                radioGroup.getChildAt(index).setEnabled(false);
            }
        }
    }

    private void setOnClickListenerResetButton(InputWidget<UIRadio, RadioGroup> widget) {
        RadioGroup radioGroup = widget.getInputView();
        UIRadio component = widget.getComponent();
        ImageView resetButton = component.getResetButton();
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                // uncheck all options
                radioGroup.clearCheck();
                for (int index = 0; index < radioGroup.getChildCount(); index++) {
                    RadioButtonWidget option = (RadioButtonWidget) radioGroup.getChildAt(index);
                    option.setChecked(false);
                }
//                widget.getConverter().setViewValue(widget.getInputView(), "");
            }
        });
    }

    private String getEntityProp(UIRadio component, UIForm form) {
        String entityProp = null;

        List<EntityMapping> mappings = form.getRepo().getMappings();
        for (EntityMapping mapping : mappings) {
            if (mapping.getProperty().equals(component.getId())) {
                entityProp = mapping.getFk();
            }
        }
        return entityProp;
    }

    @Override
    protected int getWidgetLayoutId(UIRadio component) {
        return R.layout.widget_radio;
    }

    @Override
    protected void setMessages(RenderingEnv env, InputWidget<UIRadio, RadioGroup> widget) {
        UIInputComponent component = widget.getComponent();
        String message = MessageHelper.getMessage(env, component);
        if (message != null) {
            ((TextView) ((ViewGroup) widget.getChildAt(0)).getChildAt(0)).setError(message);
        }
    }

    private static void setLayoutParams(float[] weigthts, int i, View view, int orientationType) {
        if (weigthts != null && i < weigthts.length) {
            if (orientationType == LinearLayout.HORIZONTAL) {
                view.setLayoutParams(new RadioGroup.LayoutParams(0, MATCH_PARENT, weigthts[i]));
            } else {
                view.setLayoutParams(new RadioGroup.LayoutParams(MATCH_PARENT, 0, weigthts[i]));
            }
        }
    }

}
