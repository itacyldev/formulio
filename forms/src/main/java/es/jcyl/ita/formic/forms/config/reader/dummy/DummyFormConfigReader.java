package es.jcyl.ita.formic.forms.config.reader.dummy;

import android.net.Uri;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.jcyl.ita.formic.forms.config.ComponentBuilder;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.FormConfig;
import es.jcyl.ita.formic.forms.config.builders.ComponentBuilderFactory;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.db.SQLQueryFilter;
import es.jcyl.ita.formic.repo.query.Condition;
import es.jcyl.ita.formic.repo.query.Criteria;
import es.jcyl.ita.formic.repo.query.Filter;
import es.jcyl.ita.formic.forms.config.reader.xml.XmlConfigFileReader;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.repo.query.ConditionBinding;
import es.jcyl.ita.formic.forms.repo.query.FilterHelper;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.link.UILink;
import es.jcyl.ita.formic.forms.components.link.UIParam;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.view.dag.DAGManager;

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

public class DummyFormConfigReader {
    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();
    RepositoryFactory repoFactory = RepositoryFactory.getInstance();
    //AutoCompleteBuilder autoCompleteBuilder = new AutoCompleteBuilder();

    ComponentBuilderFactory builderFactory = ComponentBuilderFactory.getInstance();


    public FormConfig read(String name, Uri uri) throws ConfigurationException {
        return read(name, (InputStream) null);
    }

    public FormConfig read(String name, InputStream is) throws ConfigurationException {

//        FormConfig formConfig = createFormTest1();

//        EditableRepository inspecRepo = repoFactory.getEditableRepo("inspecciones");
//
//        result = fcBuilder.withRepo(inspecRepo).build();
//        loadConfig(result.getEdit());
//        loadConfig(result.getList());
//
//        result = fcBuilder.withRepo(contactsRepo).withId("formContacts2").build();
//        loadConfig(result.getEdit());
//        loadConfig(result.getList());
//        createTableFilterView(result.getEdit());
//
        return createAgentsForm();
    }

    private FormConfig createFormTest1() {
        ComponentBuilder<FormConfig> builder = builderFactory.getBuilder("main", FormConfig.class);

        ConfigNode<FormConfig> node = new ConfigNode<>("main");
        node.setAttribute("id", "test1");
        node.setAttribute("repo", "contacts");
        FormConfig formConfig = builder.build(node);
        node.setElement(formConfig);
        builder.processChildren(node);
//        register(formConfig);

        // modify edit view
        FormEditController editCtl = formConfig.getEdits().get(0);
        UIView view = editCtl.getView();


        UILink link = new UILink();
        link.setId("link1");
        link.setValueExpression(exprFactory.create("Going to 3332"));
        link.setRoute(editCtl.getId());
        UIParam param = new UIParam("entityId", exprFactory.create("3332"));
        link.setParams(new UIParam[]{param});
        view.addChild(link);

        // add select to form an
        UIForm uiForm = view.getForms().get(0);
        String[] nouns = randomNouns(100);
//        for (int i = 0; i < nouns.length; i++) {
//            autoCompleteBuilder.addOption(nouns[i], nouns[i]);
//        }
//        UIAutoComplete select = autoCompleteBuilder.withValue("${entity.first_name}", String.class)
//                .withId("profileselect").withLabel("autocomplete").build();
//        uiForm.addChild(select);
//        select.setParentForm(uiForm);

        EditableRepository contactsRepo = repoFactory.getEditableRepo("contacts");

//        UIAutoComplete select2 = autoCompleteBuilder.withValue("${entity.last_name}", String.class)
//                .withId("profileselect2").withLabel("autocomplete").build();
//        select2.setRepo(contactsRepo);
//        uiForm.addChild(select2);
//        select2.setParentForm(uiForm);
//        select2.setForceSelection(true);
//        select2.setOptionLabelFilteringProperty("last_name");
//        select2.setOptionValueProperty("id");
//        select2.setOptionLabelExpression(exprFactory.create("${entity.last_name}"));
//        select2.setOptionLabelFilteringProperty("last_name");
        return formConfig;
    }

//
//    private void createTableFilterView(FormController formController) {
//        List<UIComponent> lst = new ArrayList<UIComponent>();
//
//        UIField fieldn = new UIField();
//        fieldn.setId("ini");
//        fieldn.setType(UIField.TYPE.TEXT);
//        fieldn.setLabel("ini");
//        fieldn.setReadOnly(true);
//        fieldn.setValueExpression(exprFactory.create("${entity.last_name}"));
//        lst.add(fieldn);
//
//        UIField field0 = new UIField();
//        field0.setId("f1");
//        field0.setType(UIField.TYPE.TEXT);
//        field0.setLabel("f1");
//        field0.setValueExpression(exprFactory.create("${view.ini}"));
//        lst.add(field0);
//
//
//        UIField fieldB = new UIField();
//        fieldB.setId("f2");
//        fieldB.setType(UIField.TYPE.TEXT);
//        fieldB.setLabel("f2");
//        lst.add(fieldB);
//
//        UIField field1 = new UIField();
//        field1.setId("f3");
//        field1.setType(UIField.TYPE.TEXT);
//        field1.setLabel("Filter copy");
//        field1.setValueExpression(exprFactory.create("${view.f1} - ${view.f2}"));
//        field1.setRenderExpression(exprFactory.create("${view.f1.length() < 5}"));
//        lst.add(field1);
//
//
//        String[] fieldFilter = new String[]{"contact_id", "first_name", "email"};
//        UIDatatable table = formGenerator.createDataTableFromRepo(formController.getEditableRepo(), fieldFilter);
//        table.setId("table1");
//        table.setRoute(formController.getId());
//
//        // table repository filter
//        Filter f = new SQLQueryFilter();
//        Criteria criteria = Criteria.or(
//                ConditionBinding.cond(Condition.contains("first_name", null), exprFactory.create("${view.f1}")),
//                ConditionBinding.cond(Condition.contains("email", null), exprFactory.create("${view.f1}")));
//        f.setCriteria(criteria);
//        table.setFilter(f);
//        lst.add(table);
//
//        UIForm form1 = new UIForm();
//        form1.setId("form1");
//        form1.setLabel("Formulario 1");
//        form1.setChildren(lst);
//        form1.setRenderExpression(exprFactory.create("true"));
//        form1.setRepo(formController.getEditableRepo());
//        List<UIComponent> viewKids = new ArrayList<>();
//        viewKids.add(form1);
//        UIView view1 = new UIView("view1");
//        view1.setChildren(viewKids);
//
//        DAGManager.getInstance().generateDags(view1);
//
//        formController.setView(view1);
//    }

    private FormConfig createAgentsForm() {
        XmlConfigFileReader reader = new XmlConfigFileReader();

        ConfigNode<FormConfig> node = new ConfigNode<>("main");
        node.setAttribute("id", "agents");
        node.setAttribute("repo", "agents");
        reader.build(node);
        FormConfig formConfig = node.getElement();
//        register(formConfig);


        // remove all controls but "id" and  replace them with a select and autocompletes
        FormEditController edit = formConfig.getEdits().get(0);
        UIForm uiForm = edit.getMainForm();
        List<UIComponent> toRemove = new ArrayList<>();
        for (UIComponent c : uiForm.getChildren()) {
            if (!c.getId().equalsIgnoreCase("id")) {
                toRemove.add(c);
            }
        }
        UIComponent[] kids = uiForm.getChildren();
        uiForm.removeAll();
        uiForm.addChild(kids[0]);

        // province spinner
        Repository provRepo = repoFactory.getRepo("provincia");

//        UIAutoComplete provAuto = autoCompleteBuilder.withId("provincia")
//                .withValue("${entity.provmuni.substring(0,2)}", Integer.class)
//                .withLabel("provincia").build();
//        provAuto.setOptions(null);
//        provAuto.setRepo(provRepo);
//        uiForm.addChild(provAuto);
//        provAuto.setParentForm(uiForm);
//        provAuto.setForceSelection(true);
//        provAuto.setOptionValueProperty("id");
//        provAuto.setOptionLabelExpression(exprFactory.create("${entity.name}"));
//        provAuto.setOptionLabelFilteringProperty("name");

        // council province-dependant autocomplete
        Repository muniRepo = repoFactory.getRepo("municipio");
//        UIAutoComplete muniAuto = autoCompleteBuilder.withValue("${entity.provmuni}", String.class)
//                .withId("municipio").withLabel("municipio").build();
//        muniAuto.setOptions(null);
//        muniAuto.setRepo(muniRepo);
//        uiForm.addChild(muniAuto);
//        muniAuto.setParentForm(uiForm);
//        muniAuto.setForceSelection(true);
//        muniAuto.setMandatoryFilters(new String[]{"view.provincia"});
//        muniAuto.setOptionValueProperty("provmuni");
//        muniAuto.setOptionLabelExpression(exprFactory.create("${entity.name}"));
//        muniAuto.setOptionLabelFilteringProperty("name");
//        muniAuto.addValidator(new RequiredValidator());
        // muni values depend on selected province
        Filter f = new SQLQueryFilter();
        Filter muniFilter = FilterHelper.createInstance(muniRepo);
        Criteria criteria = Criteria.and(
                ConditionBinding.cond(Condition.eq("prov", null), exprFactory.create("${view.provincia}")),
                ConditionBinding.cond(Condition.contains("name", null), exprFactory.create("${this.value}")));
        muniFilter.setExpression(criteria);
//        muniAuto.setFilter(muniFilter);

        // agents autocomplete
        Repository agents = repoFactory.getRepo("contacts");
//        UIAutoComplete agentsAC = autoCompleteBuilder.withValue("${entity.contact_id}", Integer.class)
//                .withId("agent").withLabel("agent").build();
//        agentsAC.setRepo(agents);
//        uiForm.addChild(agentsAC);
//        agentsAC.setParentForm(uiForm);
//
//        agentsAC.setForceSelection(true);
//        agentsAC.setOptionValueProperty("contact_id");
//        agentsAC.setOptionLabelExpression(exprFactory.create("${entity.contact_id} - ${entity.first_name}, ${entity.last_name}"));
//        agentsAC.setOptionLabelFilteringProperty("name");

        Filter agentFilter = FilterHelper.createInstance(agents);
        criteria = Criteria.or(
                ConditionBinding.cond(Condition.eq("first_name", null), exprFactory.create("${this.value}")),
                ConditionBinding.cond(Condition.contains("last_name", null), exprFactory.create("${this.value}")));
        agentFilter.setExpression(criteria);
//        agentsAC.setFilter(agentFilter);
        DAGManager.getInstance().generateDags(edit.getView());
//        ViewDAG viewDAG = DAGManager.getInstance().getViewDAG(edit.getView().getId());
//        System.out.println(viewDAG.getDags());

        return formConfig;
    }

//
//    private void createEditView2(FormController formController) {
//        List<UIComponent> lst = new ArrayList<UIComponent>();
//        UIField field0 = new UIField();
//        field0.setId("contactId");
//        field0.setType(UIField.TYPE.TEXT);
//        field0.setLabel("Id");
//        field0.setValueExpression(exprFactory.create("${entity.contact_id}", Long.class));
//        lst.add(field0);
//
//        UIField field1 = new UIField();
//        field1.setType(UIField.TYPE.TEXT);
//        field1.setLabel("FirstName");
//        field1.setId("firstName");
//        field1.setValueExpression(exprFactory.create("${entity.first_name}"));
//        field1.addValidator(new RequiredValidator());
//        lst.add(field1);
//
//        UIField field6 = new UIField();
//        field6.setType(UIField.TYPE.TEXT);
//        field6.setLabel("Last Name");
//        field6.setId("lastName");
//        field6.setValueExpression(exprFactory.create("${entity.last_name}"));
//        field6.addValidator(new RequiredValidator());
//        lst.add(field6);
//
//        UIField field4 = new UIField();
//        field4.setId("email");
//        field4.setType(UIField.TYPE.TEXT);
//        field4.setLabel("Email");
//        field4.setValueExpression(exprFactory.create("${entity.email}"));
//        field4.addValidator(new CommonsValidatorWrapper(EmailValidator.getInstance()));
//        field4.setRenderExpression(exprFactory.create("${entity.it_profile}"));
//        lst.add(field4);
//
//        UIField field2 = new UIField();
//        field2.setId("f2");
//        field2.setType(UIField.TYPE.BOOLEAN);
//        field2.setLabel("IT profile");
//        field2.setValueExpression(exprFactory.create("${entity.it_profile}", Long.class));
//        lst.add(field2);
//
//        UIField field3 = new UIField();
//        field3.setId("salary");
//        field3.setType(UIField.TYPE.TEXT);
//        field3.setLabel("Salary");
//        field3.setValueExpression(exprFactory.create("${entity.salary}", Double.class));
//        lst.add(field3);
//
//        // datatable
//        RepositoryConfReader config = new RepositoryConfReader("");
//        config.read();
//        EditableRepository contactsRepo = repoFactory.getEditableRepo("contacts");
////        Repository contactsRepo = repoFactory.getRepo("filteredContacts");
////        contactsRepo.setContext(MainController.getInstance().getGlobalContext());
//        String[] fieldFilter = new String[]{"contact_id", "first_name", "email"};
//        UIDatatable table = formGenerator.createDataTableFromRepo(contactsRepo, fieldFilter);
//        List<String> deps = new ArrayList<>();
//        deps.add("view.f0");
//        ContextToRepoBinding.getInstance().setRepoContextDeps(contactsRepo.getId(), deps);
//        table.setId("table1");
//        table.setRepo(contactsRepo);
//        table.setRoute("MyForm1#edit");
//        // order the table by a fixed criteria
//        Filter f = new SQLQueryFilter();
//        Criteria criteria = Criteria.or(Condition.contains("first_name", "%a%"),
//                Condition.contains("last_name", "%a%"));
//        f.setCriteria(criteria);
//        f.setSorting(new Sort[]{Sort.asc("email")});
//        table.setFilter(f);
//        lst.add(table);
//
//        UIForm form1 = new UIForm();
//        form1.setId("form1");
//        form1.setLabel("Formulario 1");
//        form1.setChildren(lst);
//        form1.setRenderExpression(exprFactory.create("true"));
//        form1.setRepo(contactsRepo);
//        List<UIComponent> lstView = new ArrayList<>();
//        lstView.add(form1);
//        UIView view1 = new UIView("view2");
//        view1.setChildren(lstView);
//
//        DAGManager.getInstance().generateDags(view1);
//
//        formController.setView(view1);
//    }

//
//    private void createListView1(FormController formController) {
//        List<UIComponent> lst = new ArrayList<UIComponent>();
//
//
//        EditableRepository contactsRepo = repoFactory.getEditableRepo("contacts");
////        Repository contactsRepo = repoFactory.getRepo("filteredContacts");
//        String[] fieldFilter = new String[]{"first_name", "email", "it_profile"};
//        UIDatatable table = formGenerator.createDataTableFromRepo(contactsRepo, fieldFilter);
//        // add new column with two fields calc
//        UIColumn newCol = new UIColumn();
//        newCol.setId("calc");
//        newCol.setHeaderText("Name");
//        newCol.setValueExpression(exprFactory.create("${entity.first_name} ${entity.last_name}"));
//        table.getColumns()[0] = newCol;
//        table.setId("table1");
//        RepositoryConfReader config = new RepositoryConfReader("");
//        config.read();
//        table.setRepo(contactsRepo);
//        table.setRoute(formController.getId() + "#edit");
//        lst.add(table);
//
//        UIView view1 = new UIView("view1List");
////        view1.setChildren(f);
//        view1.setChildren(new UIComponent[]{table});
//
//        formController.setView(view1);
//
//    }

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

