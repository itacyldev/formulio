package es.jcyl.ita.frmdrd.configuration.parser;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.crtrepo.db.SQLQueryFilter;
import es.jcyl.ita.crtrepo.query.Condition;
import es.jcyl.ita.crtrepo.query.Criteria;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.crtrepo.query.Sort;
import es.jcyl.ita.frmdrd.builders.DataTableBuilder;
import es.jcyl.ita.frmdrd.builders.FormControllerBuilder;
import es.jcyl.ita.frmdrd.configuration.ContextToRepoBinding;
import es.jcyl.ita.frmdrd.configuration.RepositoryProjectConfReader;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.repo.query.ConditionBinding;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.UIField;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumn;
import es.jcyl.ita.frmdrd.ui.components.datatable.UIDatatable;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;
import es.jcyl.ita.frmdrd.validation.CommonsValidatorWrapper;
import es.jcyl.ita.frmdrd.validation.RequiredValidator;
import es.jcyl.ita.frmdrd.view.dag.DAGManager;

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
    ValueExpressionFactory exprFactory = new ValueExpressionFactory();
    DataTableBuilder formGenerator = new DataTableBuilder();
    RepositoryFactory repoFactory = RepositoryFactory.getInstance();
    FormControllerBuilder fcBuilder = new FormControllerBuilder();

    @Override
    public void parseFormConfig(String formConfigStr) {
        FormController fc1 = new FormController("MyForm1", "Form number 1.");
        createListView1(fc1);
        createEditView1(fc1);
        loadConfig(fc1);

//        FormController fc2 = new FormController("MyForm2", "Form number 2.");
//        createListView1(fc2);
//        createEditView2(fc2);
//        loadConfig(fc2);

        EditableRepository contactsRepo = repoFactory.getEditableRepo("contacts");
        FormController fc3 = fcBuilder.withRepo(contactsRepo).build();
        loadConfig(fc3);

        EditableRepository inspecRepo = repoFactory.getEditableRepo("inspecciones");
        FormController fc4 = fcBuilder.withRepo(inspecRepo).build();
        loadConfig(fc4);
    }


    private void createEditView1(FormController formController) {
        List<UIComponent> lst = new ArrayList<UIComponent>();

        UIField fieldn = new UIField();
        fieldn.setId("initial");
        fieldn.setType(UIField.TYPE.TEXT);
        fieldn.setLabel("Inicial");
        fieldn.setValueExpression(exprFactory.create("${entity.last_name}"));
        lst.add(fieldn);

        UIField field0 = new UIField();
        field0.setId("filter");
        field0.setType(UIField.TYPE.TEXT);
        field0.setLabel("Filter");
        field0.setValueExpression(exprFactory.create("${view.initial}"));
        lst.add(field0);


        UIField fieldB = new UIField();
        fieldB.setId("f2");
        fieldB.setType(UIField.TYPE.TEXT);
        fieldB.setLabel("data");
        lst.add(fieldB);

        UIField field1 = new UIField();
        field1.setId("filtercopy");
        field1.setType(UIField.TYPE.TEXT);
        field1.setLabel("Filter copy");
        field1.setValueExpression(exprFactory.create("${view.filter} - ${view.f2}"));
        field1.setRenderExpression(exprFactory.create("${view.filter.length() < 5}"));
        lst.add(field1);


        // datatable
        RepositoryProjectConfReader config = new RepositoryProjectConfReader();
        config.read();
        EditableRepository contactsRepo = repoFactory.getEditableRepo("contacts");

        String[] fieldFilter = new String[]{"contact_id", "first_name", "email"};
        UIDatatable table = formGenerator.createDataTableFromRepo(contactsRepo, fieldFilter);

        table.setId("table1");
        table.setRepo(contactsRepo);
        table.setRoute(formController.getId() + "#edit");

        // table repository filter
        Filter f = new SQLQueryFilter();
        Criteria criteria = Criteria.or(ConditionBinding.cond(Condition.contains("first_name", null), exprFactory.create("${view.filter}")),
                ConditionBinding.cond(Condition.contains("email", null), exprFactory.create("${view.filter}")));
        f.setCriteria(criteria);
        table.setFilter(f);
        lst.add(table);


        UIForm form1 = new UIForm();
        form1.setId("form1");
        form1.setLabel("Formulario 1");
        form1.setChildren(lst);
        form1.setRenderExpression(exprFactory.create("true"));
        form1.setRepo(contactsRepo);
        List<UIComponent> viewKids = new ArrayList<>();
        viewKids.add(form1);
        UIView view1 = new UIView("view1");
        view1.setChildren(viewKids);

        DAGManager.getInstance().generateDags(view1);

        formController.setEditView(view1);
    }

    private void createEditView2(FormController formController) {
        List<UIComponent> lst = new ArrayList<UIComponent>();
        UIField field0 = new UIField();
        field0.setId("contactId");
        field0.setType(UIField.TYPE.TEXT);
        field0.setLabel("Id");
        field0.setValueExpression(exprFactory.create("${entity.contact_id}", Long.class));
        lst.add(field0);

        UIField field1 = new UIField();
        field1.setType(UIField.TYPE.TEXT);
        field1.setLabel("FirstName");
        field1.setId("firstName");
        field1.setValueExpression(exprFactory.create("${entity.first_name}"));
        field1.addValidator(new RequiredValidator());
        lst.add(field1);

        UIField field6 = new UIField();
        field6.setType(UIField.TYPE.TEXT);
        field6.setLabel("Last Name");
        field6.setId("lastName");
        field6.setValueExpression(exprFactory.create("${entity.last_name}"));
        field6.addValidator(new RequiredValidator());
        lst.add(field6);

        UIField field4 = new UIField();
        field4.setId("email");
        field4.setType(UIField.TYPE.TEXT);
        field4.setLabel("Email");
        field4.setValueExpression(exprFactory.create("${entity.email}"));
        field4.addValidator(new CommonsValidatorWrapper(EmailValidator.getInstance()));
        field4.setRenderExpression(exprFactory.create("${entity.it_profile}"));
        lst.add(field4);

        UIField field2 = new UIField();
        field2.setId("f2");
        field2.setType(UIField.TYPE.BOOLEAN);
        field2.setLabel("IT profile");
        field2.setValueExpression(exprFactory.create("${entity.it_profile}", Long.class));
        lst.add(field2);

        UIField field3 = new UIField();
        field3.setId("salary");
        field3.setType(UIField.TYPE.TEXT);
        field3.setLabel("Salary");
        field3.setValueExpression(exprFactory.create("${entity.salary}", Double.class));
        lst.add(field3);

        // datatable
        RepositoryProjectConfReader config = new RepositoryProjectConfReader();
        config.read();
        EditableRepository contactsRepo = repoFactory.getEditableRepo("contacts");
//        Repository contactsRepo = repoFactory.getRepo("filteredContacts");
//        contactsRepo.setContext(MainController.getInstance().getGlobalContext());
        String[] fieldFilter = new String[]{"contact_id", "first_name", "email"};
        UIDatatable table = formGenerator.createDataTableFromRepo(contactsRepo, fieldFilter);
        List<String> deps = new ArrayList<>();
        deps.add("view.f0");
        ContextToRepoBinding.getInstance().setRepoContextDeps(contactsRepo.getId(), deps);
        table.setId("table1");
        table.setRepo(contactsRepo);
        table.setRoute("MyForm1#edit");
        // order the table by a fixed criteria
        Filter f = new SQLQueryFilter();
        Criteria criteria = Criteria.or(Condition.contains("first_name", "%a%"),
                Condition.contains("last_name", "%a%"));
        f.setCriteria(criteria);
        f.setSorting(new Sort[]{Sort.asc("email")});
        table.setFilter(f);
        lst.add(table);

        UIForm form1 = new UIForm();
        form1.setId("form1");
        form1.setLabel("Formulario 1");
        form1.setChildren(lst);
        form1.setRenderExpression(exprFactory.create("true"));
        form1.setRepo(contactsRepo);
        List<UIComponent> lstView = new ArrayList<>();
        lstView.add(form1);
        UIView view1 = new UIView("view2");
        view1.setChildren(lstView);

        DAGManager.getInstance().generateDags(view1);

        formController.setEditView(view1);
    }

    private void createListView1(FormController formController) {
        List<UIComponent> lst = new ArrayList<UIComponent>();


        EditableRepository contactsRepo = repoFactory.getEditableRepo("contacts");
//        Repository contactsRepo = repoFactory.getRepo("filteredContacts");
        String[] fieldFilter = new String[]{"first_name", "email", "it_profile"};
        UIDatatable table = formGenerator.createDataTableFromRepo(contactsRepo, fieldFilter);
        // add new column with two fields calc
        UIColumn newCol = new UIColumn();
        newCol.setId("calc");
        newCol.setHeaderText("Name");
        newCol.setValueExpression(exprFactory.create("${entity.first_name} ${entity.last_name}"));
        table.getColumns()[0] = newCol;
        table.setId("table1");
        RepositoryProjectConfReader config = new RepositoryProjectConfReader();
        config.read();
        table.setRepo(contactsRepo);
        table.setRoute(formController.getId() + "#edit");
        lst.add(table);

//        UIForm form1 = new UIForm();
//        form1.setId("form1");
//        form1.setLabel("Formulario 1");
//        form1.setChildren(lst);
//        form1.setRepo(contactsRepo);
//        List<UIComponent> f = new ArrayList<>();
//        f.add(form1);

        UIView view1 = new UIView("view1List");
//        view1.setChildren(f);
        view1.setChildren(new UIComponent[]{table});

        formController.setListView(view1);

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

