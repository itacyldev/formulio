package es.jcyl.ita.frmdrd.validation;
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

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;


/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class RequiredValidator implements Validator {

    @Override
    public void validate(Context ctx, UIComponent component, String value) {
        //TODO: use localize messages
        String msg = "You have to fill the value.";
        if (StringUtils.isBlank(value)) {
            throw new ValidatorException(msg);
        }
    }
}
