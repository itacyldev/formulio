package es.jcyl.ita.formic.forms.components.view;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
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

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.forms.components.form.WidgetContextHolder;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ViewWidget extends Widget<UIView> {

    private List<WidgetContextHolder> contextHolders;

    public ViewWidget(Context context) {
        super(context);
    }

    public ViewWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ViewWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void registerContextHolder(WidgetContextHolder widget) {
        if (this.contextHolders == null) {
            this.contextHolders = new ArrayList<>();
        }
        this.contextHolders.add(widget);
    }

    public List<WidgetContextHolder> getContextHolders() {
        return contextHolders;
    }
}
