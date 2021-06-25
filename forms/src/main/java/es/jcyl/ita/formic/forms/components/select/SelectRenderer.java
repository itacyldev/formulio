package es.jcyl.ita.formic.forms.components.select;

import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.events.Event;
import es.jcyl.ita.formic.forms.actions.events.UserEventInterceptor;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.option.UIOption;
import es.jcyl.ita.formic.forms.components.option.UIOptionsAdapterHelper;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.InputRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHelper;

import static android.view.View.inflate;

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

public class SelectRenderer extends InputRenderer<UISelect, Spinner> {
    private static final EmptyOption EMPTY_OPTION = new EmptyOption(null, null);

    @Override
    protected void composeInputView(RenderingEnv env, InputWidget<UISelect, Spinner> widget) {
        Spinner input = widget.getInputView();
        UISelect component = widget.getComponent();

        // setup adapter and event handler
        ArrayAdapter<UIOption> arrayAdapter = UIOptionsAdapterHelper.createAdapterFromOptions(env.getAndroidContext(),
                component.getOptions(), component.hasNullOption(), android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.notifyDataSetChanged();

        input.setAdapter(arrayAdapter);
        input.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // notify action
                UserEventInterceptor interceptor = env.getUserActionInterceptor();
                if (interceptor != null) {
                    interceptor.notify(Event.inputChange(widget));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ImageView infoButton = ViewHelper.findViewAndSetId(widget, R.id.field_layout_info,
                ImageView.class);
        if (component.getHint(env.getWidgetContext()) == null) {
            infoButton.setVisibility(View.INVISIBLE);
        }
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(env.getAndroidContext(), R.style.DialogStyle);
                final View view = inflate(env.getAndroidContext(), R.layout.info_dialog, null);
                TextView titleView = view.findViewById(R.id.info);
                titleView.setText(component.getHint(env.getWidgetContext()));
                builder.setCustomTitle(view)
                        .setPositiveButton("OK", null);
                Dialog dialog = builder.create();
                dialog.show();
            }
        });

        ImageView resetButton = ViewHelper.findViewAndSetId(widget, R.id.field_layout_x,
                ImageView.class);
        if ((Boolean) ConvertUtils.convert(widget.getComponent().isReadonly(env.getWidgetContext()), Boolean.class) || !widget.getComponent().hasDeleteButton()) {
            resetButton.setVisibility(View.GONE);
        }
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                input.setSelection(0);
            }
        });

        setVisibiltyButtonLayout(StringUtils.isNotBlank(widget.getComponent().getLabel()), resetButton, infoButton);
    }

    @Override
    protected int getWidgetLayoutId(UISelect component) {
        return R.layout.widget_select;
    }

    @Override
    protected void setMessages(RenderingEnv env, InputWidget<UISelect, Spinner> widget) {
        UIInputComponent component = widget.getComponent();
        String message = WidgetContextHelper.getMessage(env.getWidgetContext(), component.getId());
        if (message != null) {
            ((TextView)((LinearLayout)widget.getChildAt(0)).getChildAt(0)).setError(message);
        }
    }

    public static class EmptyOption extends UIOption {
        public EmptyOption(String label, String value) {
            super(label, value);
        }

        @Override
        public String toString() {
            return " ";
        }
    }

}
