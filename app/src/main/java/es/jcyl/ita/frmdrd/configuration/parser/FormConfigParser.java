package es.jcyl.ita.frmdrd.configuration.parser;

import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.forms.FormControllerFactory;

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

public abstract class FormConfigParser {
    FormControllerFactory controllerFactory = FormControllerFactory.getInstance();

    /**
     * @param formConfigStr
     * @return the form's ID
     */
    public abstract void parseFormConfig(String formConfigStr);

    protected void loadConfig(FormController form) {
        controllerFactory.register(form);
    }
}
