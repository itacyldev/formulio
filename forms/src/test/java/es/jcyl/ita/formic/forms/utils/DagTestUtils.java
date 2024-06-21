package es.jcyl.ita.formic.forms.utils;
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

import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.view.dag.DAGManager;

import static es.jcyl.ita.formic.forms.config.DevConsole.info;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class DagTestUtils {


    public static void registerDags(FormConfig config) {
        info("Registering DAGS...");
        DAGManager dagManager = DAGManager.getInstance();
        FormListController formList = config.getList();
        if (formList != null) {
            dagManager.generateDags(formList.getView());
        }
        for (ViewController f : config.getEdits()) {
            dagManager.generateDags(f.getView());
        }
    }
}
