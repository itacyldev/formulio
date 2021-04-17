package es.jcyl.ita.formic.forms.components.tab;

import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.components.UIGroupComponent;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.view.ViewConfigException;

/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public class UITabItem extends UIGroupComponent {

    private ValueBindingExpression selected;

    public UITabItem() {
        this.setRendererType("tabitem");
        this.setRenderChildren(true);
    }

    public boolean isSelected(Context context) {
        if (this.selected == null) {
            return false;
        } else {
            // evaluate expression against context
            Object value = JexlFormUtils.eval(context, this.selected);
            try {
                return (Boolean) ConvertUtils.convert(value, Boolean.class);
            } catch (Exception e) {
                throw new ViewConfigException(String.format("Invalid rendering expression in " +
                                "component [%s] the resulting value couldn't be cast to Boolean.",
                        this.getId(), this.selected, e));
            }
        }
    }

    public void setSelected(ValueBindingExpression selected) {
        this.selected = selected;
    }
}
