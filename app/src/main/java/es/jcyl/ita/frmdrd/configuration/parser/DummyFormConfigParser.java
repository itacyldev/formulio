package es.jcyl.ita.frmdrd.configuration.parser;

import es.jcyl.ita.frmdrd.ui.form.UITab;
import es.jcyl.ita.frmdrd.ui.form.UIField;
import es.jcyl.ita.frmdrd.ui.form.UIForm;

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

        UIField UIField_1 = new UIField();
        UIField_1.setType(UIField.TYPE.TEXT);
        UIField_1.setLabel("campo 1");
        UIField_1.setId("campo1");

        UIField UIField_2 = new UIField();
        UIField_2.setType(UIField.TYPE.BOOLEAN);
        UIField_2.setLabel("campo 2");
        UIField_2.setId("campo2");
        UIField_2.setUpdate("campo3");

        UIField UIField_3 = new UIField();
        UIField_3.setType(UIField.TYPE.TEXT);
        UIField_3.setLabel("campo 3");
        UIField_3.setId("campo3");
        UIField_3.setRenderCondition("ctx[\"form.campo2\"] == true");

        UIField UIField_4 = new UIField();
        UIField_4.setType(UIField.TYPE.TEXT);
        UIField_4.setLabel("campo 4");
        UIField_4.setId("campo4");
        UIField_4.setSource("");

        UITab tab1_1 = new UITab();
        tab1_1.setId("tab1");
        tab1_1.addField(UIField_1);
        tab1_1.addField(UIField_2);
        tab1_1.addField(UIField_3);
        tab1_1.addField(UIField_4);


        UIForm form1 = new UIForm();
        form1.setId("Form_1");
        form1.setLabel("Formulario 1");
        form1.addTab(tab1_1);

        loadConfig(form1);

        UIField UIField_2_1 = new UIField();
        UIField_2_1.setType(UIField.TYPE.TEXT);
        UIField_2_1.setLabel("campo 1");
        UIField_2_1.setId("campo1");

        UIField UIField_2_2 = new UIField();
        UIField_2_2.setType(UIField.TYPE.BOOLEAN);
        UIField_2_2.setLabel("campo 2");
        UIField_2_2.setId("campo2");
        UIField_2_2.setUpdate("campo3");

        UIField UIField_2_3 = new UIField();
        UIField_2_3.setType(UIField.TYPE.TEXT);
        UIField_2_3.setLabel("campo 3");
        UIField_2_3.setId("campo3");
        UIField_2_3.setRenderCondition("ctx[\"form.campo2\"] == true");

        UIField UIField_2_4 = new UIField();
        UIField_2_4.setType(UIField.TYPE.DATE);
        UIField_2_4.setLabel("campo 4");
        UIField_2_4.setId("campo4");

        UITab tab_2_1_1 = new UITab();
        tab_2_1_1.setId("tab1");
        tab_2_1_1.addField(UIField_2_1);
        tab_2_1_1.addField(UIField_2_2);
        tab_2_1_1.addField(UIField_2_3);
        tab_2_1_1.addField(UIField_2_4);


        UIForm form2 = new UIForm();
        form2.setId("Form_2");
        form2.setLabel("Formulario 2");
        form2.addTab(tab_2_1_1);

        //loadConfig(form2);

        return form1.getId();
    }
}

