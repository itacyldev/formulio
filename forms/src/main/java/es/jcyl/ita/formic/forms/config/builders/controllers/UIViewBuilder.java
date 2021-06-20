package es.jcyl.ita.formic.forms.config.builders.controllers;
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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.buttonbar.UIButtonBar;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.builders.ui.UIGroupComponentBuilder;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * Defines UIView configuration from XML node structure.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIViewBuilder extends UIGroupComponentBuilder<UIView> {

    public UIViewBuilder() {
        super("view", UIView.class);
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<UIView> node) {
        UIComponent[] uiComponents = ConfigNodeHelper.getUIChildren(node);

        UIView view = node.getElement();
        // Look for defined view buttonbars (fab,bottom,menu) and set them in the UIView element
        List<UIComponent> toRemove = new ArrayList<>();
        String type;
        for (UIComponent component : uiComponents) {
            if (component instanceof UIButtonBar) {
                type = ((UIButtonBar) component).getType();
                UIButtonBar.ButtonBarType buttonBarType = UIButtonBar.ButtonBarType.valueOf(type.toUpperCase());
                switch (buttonBarType) {
                    case FAB:
                        view.setFabBar((UIButtonBar) component);
                        toRemove.add(component);
                        break;
                    case MENU:
                        view.setMenuBar((UIButtonBar) component);
                        toRemove.add(component);
                        break;
                    case BOTTOM:
                        view.setBottomNav((UIButtonBar) component);
                        toRemove.add(component);
                        break;
                }
            }
        }
        // remove main buttonbars from the content of the view
        for (UIComponent element : toRemove) {
            uiComponents = ArrayUtils.removeElement(uiComponents, element);
        }
        view.setChildren(uiComponents);
    }
}
