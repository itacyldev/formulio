package es.jcyl.ita.frmdrd.builders;
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

import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.UIInputComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public abstract class BaseInputBuilder<C extends UIInputComponent> extends BaseComponentBuilder<C> {

    public BaseInputBuilder() {
        super();
    }

    public BaseInputBuilder<C> withLabel(String label) {
        this.baseModel.setLabel(label);
        return this;
    }


    public BaseInputBuilder<C> withId(String id) {
        this.baseModel.setId(id);
        return this;
    }

    public BaseInputBuilder<C> withValue(String valueExpression, Class expectedType) {
        this.baseModel.setValueExpression(exprFactory.create(valueExpression, expectedType));
        return this;
    }

    public BaseInputBuilder<C> withRender(String renderExpression) {
        this.baseModel.setRenderExpression(exprFactory.create(renderExpression));
        return this;
    }
}