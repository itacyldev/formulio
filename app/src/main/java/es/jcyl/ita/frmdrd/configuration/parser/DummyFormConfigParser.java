package es.jcyl.ita.frmdrd.configuration.parser;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.frmdrd.repo.RepositoryProjectConfReader;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.UIField;
import es.jcyl.ita.frmdrd.ui.components.UIForm;
import es.jcyl.ita.frmdrd.ui.components.datatable.UIDatatable;

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

public class DummyFormConfigParser extends FormConfigParser {
    @Override
    public String parseFormConfig(String formConfigStr) {
        List<UIComponent> lst = new ArrayList<UIComponent>();
        UIField UIField_1 = new UIField();
        UIField_1.setType(UIField.TYPE.TEXT);
        UIField_1.setLabel("campo 1");
        UIField_1.setId("campo1");
        lst.add(UIField_1);

        UIField UIField_2 = new UIField();
        UIField_2.setType(UIField.TYPE.BOOLEAN);
        UIField_2.setLabel("campo 2");
        UIField_2.setId("campo2");
        UIField_2.setUpdate("campo3");
        lst.add(UIField_2);

        UIField UIField_3 = new UIField();
        UIField_3.setType(UIField.TYPE.TEXT);
        UIField_3.setLabel("campo 3");
        UIField_3.setId("campo3");
        UIField_3.setRenderCondition("ctx[\"form.campo2\"] == true");
        lst.add(UIField_3);

        UIDatatable table = new UIDatatable();
        table.setId("table1");
        RepositoryProjectConfReader config = new RepositoryProjectConfReader();
        config.read();
        RepositoryFactory repoFactory = RepositoryFactory.getInstance();
        EditableRepository contactsRepo = repoFactory.getEditableRepo("contacts");
//        Repository contactsRepo = repoFactory.getRepo("filteredContacts");
        table.setRepo(contactsRepo);
        lst.add(table);

        UIForm form1 = new UIForm();
        form1.setId("form1");
        form1.setLabel("Formulario 1");
        form1.setChildren(lst);

        loadConfig(form1);

        return form1.getId();
    }

//
//    public String parseFormConfig2(String formConfigStr) {
//
//        UIField UIField_1 = new UIField();
//        UIField_1.setType(UIField.TYPE.TEXT);
//        UIField_1.setLabel("campo 1");
//        UIField_1.setId("campo1");
//
//        UIField UIField_2 = new UIField();
//        UIField_2.setType(UIField.TYPE.BOOLEAN);
//        UIField_2.setLabel("campo 2");
//        UIField_2.setId("campo2");
//        UIField_2.setUpdate("campo3");
//
//        UIField UIField_3 = new UIField();
//        UIField_3.setType(UIField.TYPE.TEXT);
//        UIField_3.setLabel("campo 3");
//        UIField_3.setId("campo3");
//        UIField_3.setRenderCondition("ctx[\"form.campo2\"] == true");
//
//        UIDatatable table = new UIDatatable();
//        table.setId("table1");
//
//        UITab tab1_1 = new UITab();
//        tab1_1.setId("tab1");
//        tab1_1.addComponent(UIField_1);
//        tab1_1.addComponent(UIField_2);
//        tab1_1.addComponent(UIField_3);
//        tab1_1.addComponent(table);
//
//
//        UIForm form1 = new UIForm();
//        form1.setId("Form_1");
//        form1.setLabel("Formulario 1");
//        form1.addTab(tab1_1);
//
//        loadConfig(form1);
//
//        UIField UIField_2_1 = new UIField();
//        UIField_2_1.setType(UIField.TYPE.TEXT);
//        UIField_2_1.setLabel("campo 1");
//        UIField_2_1.setId("campo1");
//
//        UIField UIField_2_2 = new UIField();
//        UIField_2_2.setType(UIField.TYPE.BOOLEAN);
//        UIField_2_2.setLabel("campo 2");
//        UIField_2_2.setId("campo2");
//        UIField_2_2.setUpdate("campo3");
//
//        UIField UIField_2_3 = new UIField();
//        UIField_2_3.setType(UIField.TYPE.TEXT);
//        UIField_2_3.setLabel("campo 3");
//        UIField_2_3.setId("campo3");
//        UIField_2_3.setRenderCondition("ctx[\"form.campo2\"] == true");
//
//        UIField UIField_2_4 = new UIField();
//        UIField_2_4.setType(UIField.TYPE.DATE);
//        UIField_2_4.setLabel("campo 4");
//        UIField_2_4.setId("campo4");
//
//        UITab tab_2_1_1 = new UITab();
//        tab_2_1_1.setId("tab1");
//        tab_2_1_1.addComponent(UIField_2_1);
//        tab_2_1_1.addComponent(UIField_2_2);
//        tab_2_1_1.addComponent(UIField_2_3);
//        tab_2_1_1.addComponent(UIField_2_4);
//
//
//        UIForm form2 = new UIForm();
//        form2.setId("Form_2");
//        form2.setLabel("Formulario 2");
//        form2.addTab(tab_2_1_1);
//
//        //loadConfig(form2);
//
//        return form1.getId();
//    }
}

