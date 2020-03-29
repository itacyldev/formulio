package es.jcyl.ita.frmdrd.render;
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

import org.mini2Dx.beanutils.ConvertUtils;

import es.jcyl.ita.frmdrd.ui.components.UIComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public abstract class FieldRenderer extends BaseRenderer {

    protected static final Object EMPTY_STRING = "";

    /**
     * Tries to retrieve the component value first accessing the form context and then using
     * global context
     *
     * @param component
     * @param env
     * @return
     */
    protected <T> T getValue(UIComponent component, ExecEnvironment env, Class<T> clazz) {
        Object value = component.getValue(env.getCombinedContext());
        if (value == null) {
            return handleNullValue(value);
        }
        return (T) ConvertUtils.convert(value, clazz);
    }

    protected abstract <T> T handleNullValue(Object value);

}
