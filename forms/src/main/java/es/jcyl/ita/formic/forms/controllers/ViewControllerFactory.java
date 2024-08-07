package es.jcyl.ita.formic.forms.controllers;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.jcyl.ita.formic.forms.MainController;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Stores FormController instances.
 */
public class ViewControllerFactory {

    private static ViewControllerFactory instance;
    private Map<String, ViewController> formInstances;
    private MainController mc;

    public static ViewControllerFactory getInstance() {
        if (instance == null) {
            instance = new ViewControllerFactory();
        }
        return instance;
    }

    private ViewControllerFactory() {
        formInstances = new HashMap<>();
    }

    public void register(ViewController controller) {
        formInstances.put(controller.getId(), controller);
        controller.setMc(mc);
    }

    public ViewController getController(String id) {
        return formInstances.get(id);
    }

    public List<FormListController> getListControllers() {
        List<FormListController> lst = new ArrayList<>();
        for (ViewController controller : formInstances.values()) {
            if (controller instanceof FormListController) {
                lst.add((FormListController) controller);
            }
        }
        return lst;
    }
    public Set<String> getControllerIds(){
        return formInstances.keySet();
    }

    public Collection<ViewController> getList() {
        return formInstances.values();
    }

    public void clear() {
        formInstances.clear();
    }

    public void setMc(MainController mc) {
        this.mc = mc;
    }
}
