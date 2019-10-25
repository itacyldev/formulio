package es.jcyl.ita.frmdrd.configuration.parser;

import es.jcyl.ita.frmdrd.ui.form.UITab;
import es.jcyl.ita.frmdrd.ui.form.UIField;
import es.jcyl.ita.frmdrd.ui.form.UIForm;

public class DummyFormConfigParser extends FormConfigParser {
    @Override
    public String parseFormConfig(String formConfigStr) {

        UIField UIField_1 = new UIField();
        UIField_1.setType(UIField.TYPE.TEXT);
        UIField_1.setLabel("campo 1");
        UIField_1.setId("campo1");

        UIField UIField_2 = new UIField();
        UIField_2.setType(UIField.TYPE.BOOLEAN);
        UIField_2.setLabel("campo 2");
        UIField_2.setId("campo2");
        UIField_2.setUpdate("form.campo3");

        UIField UIField_3 = new UIField();
        UIField_3.setType(UIField.TYPE.TEXT);
        UIField_3.setLabel("campo 3");
        UIField_3.setId("campo3");
        UIField_3.setRenderCondition("${form.campo2 = true}");

        UIField UIField_4 = new UIField();
        UIField_4.setType(UIField.TYPE.DATE);
        UIField_4.setLabel("campo 4");
        UIField_4.setId("campo4");

        UITab tab1_1 = new UITab();
        tab1_1.setId("tab1");
        tab1_1.addField(UIField_1);
        tab1_1.addField(UIField_2);
        tab1_1.addField(UIField_3);
        tab1_1.addField(UIField_4);


        UIForm form1 = new UIForm();
        form1.setId("UIForm1");
        form1.setLabel("Formulario 1");
        form1.addTab(tab1_1);

        loadConfig(form1);

        return form1.getId();
    }
}

