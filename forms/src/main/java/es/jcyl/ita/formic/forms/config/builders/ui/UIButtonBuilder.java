package es.jcyl.ita.formic.forms.config.builders.ui;
/*
 * Copyright 2020 Rosa María Muñiz (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
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

import es.jcyl.ita.formic.forms.components.link.UIButton;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;

/**
 * @author Rosa María Muñiz (mungarro@itacyl.es)
 */
public class UIButtonBuilder extends AbstractComponentBuilder<UIButton> {

    public UIButtonBuilder() {
        super("button", UIButton.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<UIButton> node) {
        // Do nothing
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<UIButton> node) {
        // Do nothing
    }


}
