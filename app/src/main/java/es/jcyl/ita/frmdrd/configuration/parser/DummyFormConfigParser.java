package es.jcyl.ita.frmdrd.configuration.parser;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.repo.RepositoryProjectConfReader;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.datatable.UIDatatable;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;

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
        ValueExpressionFactory exprFactory = new ValueExpressionFactory();


        List<UIComponent> lst = new ArrayList<UIComponent>();
        UIField field0 = new UIField();
        field0.setId("f0");
        field0.setType(UIField.TYPE.TEXT);
        field0.setLabel("Contact id");
        field0.setValueExpression(exprFactory.create("${entity.contact_id}",Long.class));
        lst.add(field0);

        UIField field1 = new UIField();
        field1.setType(UIField.TYPE.TEXT);
        field1.setLabel("firstName");
        field1.setId("f1");
        field1.setValueExpression(exprFactory.create("${entity.first_name} ${entity.last_name}"));
        lst.add(field1);

        UIField field2 = new UIField();
        field2.setId("f2");
        field2.setType(UIField.TYPE.BOOLEAN);
        field2.setLabel("IT profile");
        field2.setValueExpression(exprFactory.create("${entity.it_profile}", Long.class));
        lst.add(field2);

        UIField field3 = new UIField();
        field3.setId("f3");
        field3.setType(UIField.TYPE.TEXT);
        field3.setLabel("Salary");
        field3.setValueExpression(exprFactory.create("${entity.salary}", Double.class));
        lst.add(field3);

        UIField field4 = new UIField();
        field4.setId("f4");
        field4.setType(UIField.TYPE.TEXT);
        field4.setLabel("campo 4");
        field4.setValueExpression(exprFactory.create("${entity.last_name}"));
        lst.add(field4);

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
        form1.setRepo(contactsRepo);
        List<UIComponent> f = new ArrayList<>();
        f.add(form1);
        UIView view1 = new UIView("view1");
        view1.setChildren(f);

        FormController c1 = new FormController("MyForm1", "Form number 1.");
        c1.setEditView(view1);

        loadConfig(c1);

        return view1.getId();

//        List<UIComponent> lst2 = new ArrayList<UIComponent>();
//        UIField field2_1 = new UIField();
//        field2_1.setType(UIField.TYPE.TEXT);
//        field2_1.setLabel("campo 1");
//        field2_1.setId("campo1");
//        lst2.add(field2_1);
//
//        UIField field2_2 = new UIField();
//        field2_2.setType(UIField.TYPE.TEXT);
//        field2_2.setLabel("campo 2");
//        field2_2.setId("campo2");
//        lst2.add(field2_2);
//
//        UIForm form2 = new UIForm();
//        form2.setId("form2");
//        form2.setLabel("Formulario 2");
//        form2.setChildren(lst2);
//
//        loadConfig(form2);
//
//
//        return form1.getId();

    }

//
//    public String parseFormConfig2(String formConfigStr) {
//
//        UIField field1 = new UIField();
//        field1.setType(UIField.TYPE.TEXT);
//        field1.setLabel("campo 1");
//        field1.setId("campo1");
//
//        UIField field2 = new UIField();
//        field2.setType(UIField.TYPE.BOOLEAN);
//        field2.setLabel("campo 2");
//        field2.setId("campo2");
//        field2.setUpdate("campo3");
//
//        UIField field3 = new UIField();
//        field3.setType(UIField.TYPE.TEXT);
//        field3.setLabel("campo 3");
//        field3.setId("campo3");
//        field3.setRenderCondition("ctx[\"form.campo2\"] == true");
//
//        UIDatatable table = new UIDatatable();
//        table.setId("table1");
//
//        UITab tab1_1 = new UITab();
//        tab1_1.setId("tab1");
//        tab1_1.addComponent(field1);
//        tab1_1.addComponent(field2);
//        tab1_1.addComponent(field3);
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
//        UIField field2_1 = new UIField();
//        field2_1.setType(UIField.TYPE.TEXT);
//        field2_1.setLabel("campo 1");
//        field2_1.setId("campo1");
//
//        UIField field2_2 = new UIField();
//        field2_2.setType(UIField.TYPE.BOOLEAN);
//        field2_2.setLabel("campo 2");
//        field2_2.setId("campo2");
//        field2_2.setUpdate("campo3");
//
//        UIField field2_3 = new UIField();
//        field2_3.setType(UIField.TYPE.TEXT);
//        field2_3.setLabel("campo 3");
//        field2_3.setId("campo3");
//        field2_3.setRenderCondition("ctx[\"form.campo2\"] == true");
//
//        UIField field2_4 = new UIField();
//        field2_4.setType(UIField.TYPE.DATE);
//        field2_4.setLabel("campo 4");
//        field2_4.setId("campo4");
//
//        UITab tab_2_1_1 = new UITab();
//        tab_2_1_1.setId("tab1");
//        tab_2_1_1.addComponent(field2_1);
//        tab_2_1_1.addComponent(field2_2);
//        tab_2_1_1.addComponent(field2_3);
//        tab_2_1_1.addComponent(field2_4);
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

