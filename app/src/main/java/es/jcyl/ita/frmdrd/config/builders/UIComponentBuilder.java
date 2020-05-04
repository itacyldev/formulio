package es.jcyl.ita.frmdrd.config.builders;
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

import org.mini2Dx.beanutils.BeanUtils;

import es.jcyl.ita.frmdrd.config.reader.AbstractComponentBuilder;
import es.jcyl.ita.frmdrd.config.reader.BaseConfigNode;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;

import static es.jcyl.ita.frmdrd.config.ConfigConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIComponentBuilder extends AbstractComponentBuilder {

    private UIComponent component;

    public UIComponentBuilder(String tagName, Class<?> clazz) {
        super(tagName);
        component = instantiate(clazz);
    }

    protected void doWithAttribute(String name, String value) {
        try {
            if("id".equals(name)
//            if("renderExpression"){
//                setexpression(name, value);
//            }
//            if(isAssignableAttribute(name)){
//                // set attribute using reflection
//                BeanUtils.setProperty(component, name, value);
//            }
        } catch (Exception e) {
            error(String.format("Error while trying to set attribute [%s] on element [${tag}].", name), e);
        }
    }

    @Override
    protected Object doBuild() {
        return this.component;
    }

    private UIComponent instantiate(Class clazz) {
        try {
            UIComponent component = (UIComponent) clazz.getDeclaredConstructor().newInstance();
            return component;
        } catch (Exception e) {
            error(String.format("Error while trying to instantiate UIComponent from class: [%s].", componentClass.getName()), e);
        }
    }
}
