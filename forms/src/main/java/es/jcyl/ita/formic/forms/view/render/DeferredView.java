package es.jcyl.ita.formic.forms.view.render;

import android.content.Context;

import org.apache.commons.lang3.RandomUtils;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.view.widget.Widget;

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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class DeferredView extends Widget {

    public DeferredView(Context context) {
        super(context);
    }
    public DeferredView(Context context, UIComponent component) {
        super(context);
        this.setId(RandomUtils.nextInt());
        this.setTag(component.getId());
        this.component =  component;
    }
}
