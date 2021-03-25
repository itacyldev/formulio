package es.jcyl.ita.formic.forms.components.autocomplete;
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
 * without warranties or conditions of any kind, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.components.DynamicComponent;
import es.jcyl.ita.formic.forms.context.ContextUtils;
import es.jcyl.ita.formic.forms.context.impl.AndViewContext;
import es.jcyl.ita.formic.forms.repo.query.FilterHelper;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.query.Filter;
import es.jcyl.ita.formic.repo.query.Sort;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class AutoCompleteWidget extends InputWidget<UIAutoComplete, AutoCompleteView>
        implements DynamicComponent {

    public AutoCompleteWidget(Context context) {
        super(context);
    }

    public AutoCompleteWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoCompleteWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void load(RenderingEnv env) {
        // reload autocomplete adapter values
        this.getInputView().load(env);
        getInputView().setValue(null);
    }

}