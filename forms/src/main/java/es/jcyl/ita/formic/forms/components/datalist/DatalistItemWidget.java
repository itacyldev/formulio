package es.jcyl.ita.formic.forms.components.datalist;
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

import es.jcyl.ita.formic.forms.components.form.WidgetContextHolder;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class DatalistItemWidget extends Widget<UIDatalistItem> implements WidgetContextHolder {

    public DatalistItemWidget(Context context) {
        super(context);
    }

    public DatalistItemWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DatalistItemWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DatalistItemWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public String getHolderId() {
        return this.getComponentId();
    }


    @Override
    public Widget getWidget() {
        return this;
    }

}