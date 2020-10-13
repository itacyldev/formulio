package es.jcyl.ita.formic.forms.repo.builders;
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

import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */


public abstract class AbstractDataBuilder<M> implements DataBuilder<M> {
    protected static ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();

    protected M baseModel;


    protected abstract M getModelInstance();
    public abstract <D extends DataBuilder<M>> D withRandomData();

    public AbstractDataBuilder(){
        this.baseModel = createEmptyModel();
    }
    /**
     *
     * @param templateModel
     * @return
     */
    protected M doBuild(M templateModel) {
        M model = createEmptyModel();
        try {
            BeanUtils.copyProperties(model, templateModel);
        } catch (Exception e) {
            throw new DataBuilderException("An error occurred while trying to copy datos from the model: "
                    + model.toString(), e);
        }
        return model;
    }

    /**************************************************/
    /** OPERACIONES GENÉRICAS DE CREACIÓN DE MODELOS **/
    /**************************************************/

    @Override
    public final M build() {
        M model = doBuild(this.baseModel);
        // limpiar el modelo plantilla
        this.baseModel = createEmptyModel();
        return model;
    }


    protected final M createEmptyModel() {
        M model = getModelInstance();
        if (model == null) {
            throw new DataBuilderException(
                    "The method getModelInstance() has returned null, make sure the method returns a valid instance.");
        }
        return model;
    }


    @Override
    public final DataBuilder<M> with(String property, Object value) {
        try {
            BeanUtils.setProperty(this.baseModel, property, value);
        } catch (Exception e) {
            throw new DataBuilderException(String.format(
                    "An error ocurred while trying to set the property %s with the value %s in the model [%s].",
                    property, value, this.baseModel));
        }
        return this;
    }


}
