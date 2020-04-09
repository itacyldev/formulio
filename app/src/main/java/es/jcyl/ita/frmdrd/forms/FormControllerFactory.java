package es.jcyl.ita.frmdrd.forms;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 *
 * Stores FormController instances.
 */
public class FormControllerFactory {

    private static FormControllerFactory instance;
    private Map<String, FormController> formInstances;

    public static FormControllerFactory getInstance() {
        if (instance == null) {
            instance = new FormControllerFactory();
        }
        return instance;
    }

    private FormControllerFactory() {
        formInstances = new HashMap<>();
    }

    public void register(FormController controller) {
        if (formInstances.containsKey(controller.getId())) {
            throw new FormException("There's already one form registered with id: " + controller.getId());
        }
        formInstances.put(controller.getId(), controller);
    }

    public FormController getController(String id) {
        if (!formInstances.containsKey(id)) {
            throw new FormException("No FormControllerInstance found with the id: " + id);
        }
        return formInstances.get(id);
    }

    public List<FormListController> getListControllers() {
        List<FormListController> lst = new ArrayList<>();
        for (FormController controller : formInstances.values()) {
            if (controller instanceof FormListController) {
                lst.add((FormListController) controller);
            }
        }
        return lst;
    }
}
