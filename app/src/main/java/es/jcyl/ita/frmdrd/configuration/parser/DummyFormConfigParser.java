package es.jcyl.ita.frmdrd.configuration.parser;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.crtrepo.db.SQLQueryFilter;
import es.jcyl.ita.crtrepo.query.Condition;
import es.jcyl.ita.crtrepo.query.Criteria;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.crtrepo.query.Sort;
import es.jcyl.ita.frmdrd.builders.AutoCompleteBuilder;
import es.jcyl.ita.frmdrd.builders.DataTableBuilder;
import es.jcyl.ita.frmdrd.builders.FormControllerBuilder;
import es.jcyl.ita.frmdrd.configuration.ContextToRepoBinding;
import es.jcyl.ita.frmdrd.configuration.RepositoryProjectConfReader;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.repo.query.ConditionBinding;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.autocomplete.UIAutoComplete;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumn;
import es.jcyl.ita.frmdrd.ui.components.datatable.UIDatatable;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.ui.components.link.UILink;
import es.jcyl.ita.frmdrd.ui.components.link.UIParam;
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
    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();
    DataTableBuilder formGenerator = new DataTableBuilder();
    RepositoryFactory repoFactory = RepositoryFactory.getInstance();
    FormControllerBuilder fcBuilder = new FormControllerBuilder();
    AutoCompleteBuilder autoCompleteBuilder = new AutoCompleteBuilder();

    @Override
    public void parseFormConfig(String formConfigStr) {
//        FormController fc1List = new FormListController("MyForm1", "Form number 1.");
//        createListView1(fc1List);
//        loadConfig(fc1List);
//
//        FormController fc1Edit = new FormListController("MyForm1", "Form number 1.");
//        createEditView1(fc1Edit);
//        loadConfig(fc1Edit);

//        FormController fc2 = new FormController("MyForm2", "Form number 2.");
//        createListView1(fc2);
//        createEditView2(fc2);
//        loadConfig(fc2);

        EditableRepository contactsRepo = repoFactory.getEditableRepo("contacts");
        FormControllerBuilder.FormBuilderResult result = fcBuilder.withRepo(contactsRepo).build();
        loadConfig(result.getEdit());
        UILink link = new UILink();
        link.setId("link1");
        link.setValueExpression(exprFactory.create("Going to 3332"));
        link.setRoute(result.getEdit().getId());
        UIParam param = new UIParam("entityId", exprFactory.create("3332"));
        link.setParams(new UIParam[]{param});
        result.getEdit().getView().addChild(link);

        // add select to form an
        UIForm uiForm = result.getEdit().getView().getForms().get(0);
        String[] nouns = randomNouns(50);
        for (int i = 0; i < 30; i++) {
            autoCompleteBuilder.addOption(nouns[i], nouns[i]);
        }
//        UIAutoComplete select = autoCompleteBuilder.withValue("${entity.it_profile}", Integer.class)
//                .withId("profileselect").withLabel("autocomplete").build();
//        uiForm.getChildren().add(0, select);
//        select.setParentForm(uiForm);

        UIAutoComplete select2 = autoCompleteBuilder.withValue("${entity.it_profile}", Integer.class)
                .withId("profileselect2").withLabel("autocomplete").build();
        select2.setRepo(contactsRepo);
        uiForm.getChildren().add(0, select2);
        select2.setParentForm(uiForm);
        select2.setOptionValueExpression(exprFactory.create("${entity.id}"));
        select2.setOptionLabelExpression(exprFactory.create("${entity.last_name}"));


        loadConfig(result.getList());

        EditableRepository inspecRepo = repoFactory.getEditableRepo("inspecciones");
        result = fcBuilder.withRepo(inspecRepo).build();
        loadConfig(result.getEdit());
        loadConfig(result.getList());

        result = fcBuilder.withRepo(contactsRepo).withId("formContacts2").build();
        loadConfig(result.getEdit());
        loadConfig(result.getList());
        createTableFilterView(result.getEdit());

    }


    private void createTableFilterView(FormController formController) {
        List<UIComponent> lst = new ArrayList<UIComponent>();

        UIField fieldn = new UIField();
        fieldn.setId("ini");
        fieldn.setType(UIField.TYPE.TEXT);
        fieldn.setLabel("ini");
        fieldn.setReadOnly(true);
        fieldn.setValueExpression(exprFactory.create("${entity.last_name}"));
        lst.add(fieldn);

        UIField field0 = new UIField();
        field0.setId("f1");
        field0.setType(UIField.TYPE.TEXT);
        field0.setLabel("f1");
        field0.setValueExpression(exprFactory.create("${view.ini}"));
        lst.add(field0);


        UIField fieldB = new UIField();
        fieldB.setId("f2");
        fieldB.setType(UIField.TYPE.TEXT);
        fieldB.setLabel("f2");
        lst.add(fieldB);

        UIField field1 = new UIField();
        field1.setId("f3");
        field1.setType(UIField.TYPE.TEXT);
        field1.setLabel("Filter copy");
        field1.setValueExpression(exprFactory.create("${view.f1} - ${view.f2}"));
        field1.setRenderExpression(exprFactory.create("${view.f1.length() < 5}"));
        lst.add(field1);


        String[] fieldFilter = new String[]{"contact_id", "first_name", "email"};
        UIDatatable table = formGenerator.createDataTableFromRepo(formController.getEditableRepo(), fieldFilter);
        table.setId("table1");
        table.setRoute(formController.getId());

        // table repository filter
        Filter f = new SQLQueryFilter();
        Criteria criteria = Criteria.or(
                ConditionBinding.cond(Condition.contains("first_name", null), exprFactory.create("${view.f1}")),
                ConditionBinding.cond(Condition.contains("email", null), exprFactory.create("${view.f1}")));
        f.setCriteria(criteria);
        table.setFilter(f);
        lst.add(table);

        UIForm form1 = new UIForm();
        form1.setId("form1");
        form1.setLabel("Formulario 1");
        form1.setChildren(lst);
        form1.setRenderExpression(exprFactory.create("true"));
        form1.setRepo(formController.getEditableRepo());
        List<UIComponent> viewKids = new ArrayList<>();
        viewKids.add(form1);
        UIView view1 = new UIView("view1");
        view1.setChildren(viewKids);

        DAGManager.getInstance().generateDags(view1);

        formController.setView(view1);
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

        formController.setView(view1);
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

        UIView view1 = new UIView("view1List");
//        view1.setChildren(f);
        view1.setChildren(new UIComponent[]{table});

        formController.setView(view1);

    }

    /******************************************/
    /** PARA ELIMINAR **/
    /****************************************/

    public static int randomInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static String[] randomNouns(int size) {
        String[] nouns = new String[size];
        for (int i = 0; i < size; i++) {
            int pos = randomInt(0, _nouns.length - 1);
            nouns[i] = _nouns[pos];
        }
        return nouns;
    }

    private static final String[] _nouns = new String[]{"belt", "blouse", "boots", "cap", "cardigan", "coat", "dress", "gloves", "hat", "jacket", "jeans", "jumper", "mini", "skirt", "overalls", "overcoat", "pijamas", "pants", "pantyhose", "raincoat", "scarf", "shirt", "shoes", "shorts", "skirt", "slacks", "slippers", "socks", "stockings", "suit", "sweat", "shirt", "sweater", "sweatshirt", "tie", "trousers", "underclothes", "underpants", "undershirt", "vest", "ankle", "arm", "back", "beard", "blood", "body", "bone", "brain", "cheek", "chest", "chin", "ear", "ears", "elbow", "eye", "eyes", "face", "feet", "finger", "fingers", "flesh", "foot", "hair", "hand", "hands", "head", "heart", "hip", "knee", "knees", "leg", "legs", "lip", "moustache", "mouth", "muscle", "nail", "neck", "nose", "shoulder", "shoulders", "skin", "stomach", "teeth", "throat", "thumb", "thumbs", "toe", "toes", "tongue", "tooth", "wrist", "alligator", "ant", "bear", "bee", "bird", "camel", "cat", "cheetah", "chicken", "chimpanzee", "cow", "crocodile", "deer", "dog", "dolphin", "duck", "eagle", "elephant", "fish", "fly", "fox", "frog", "giraffe", "goat", "goldfish", "hamster", "hippopotamus", "horse", "kangaroo", "kitten", "leopard", "lion", "lizard", "lobster", "monkey", "octopus", "ostrich", "otter", "owl", "oyster", "panda", "parrot", "pelican", "pig", "pigeon", "porcupine", "puppy", "rabbit", "rat", "reindeer", "rhinoceros", "rooster", "scorpion", "seal", "shark", "sheep", "shrimp", "snail", "snake", "sparrow", "spider", "squid", "squirrel", "swallow", "swan", "tiger", "toad", "tortoise", "turtle", "vulture", "walrus", "weasel", "whale", "wolf", "zebra"};

}

