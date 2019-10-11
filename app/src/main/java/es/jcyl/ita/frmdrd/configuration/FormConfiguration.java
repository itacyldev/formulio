package es.jcyl.ita.frmdrd.configuration;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.frmdrd.ui.form.Field;
import es.jcyl.ita.frmdrd.ui.form.Form;
import es.jcyl.ita.frmdrd.ui.form.Tab;

public class FormConfiguration {

    private static Map<String, Form> Forms = new HashMap<String, Form>();

    private static void load() {
        Field field_1 = new Field();
        field_1.setType(Field.TYPE.TEXT);
        field_1.setName("campo 1");
        field_1.setId("campo1");

        Field field_2 = new Field();
        field_2.setType(Field.TYPE.BOOLEAN);
        field_2.setName("campo 2");
        field_2.setId("campo2");
        field_2.setRerender("this.campo3");

        Field field_3 = new Field();
        field_3.setType(Field.TYPE.TEXT);
        field_3.setName("campo 3");
        field_3.setId("campo3");
        field_3.setRenderCondition("${this.campo2 = true}");

        Field field_4 = new Field();
        field_4.setType(Field.TYPE.DATE);
        field_4.setName("campo 4");
        field_4.setId("campo4");

        Tab tab1_1 = new Tab();
        tab1_1.setId("tab1");
        tab1_1.addField(field_1);
        tab1_1.addField(field_2);
        tab1_1.addField(field_3);
        tab1_1.addField(field_4);


        Form form1 = new Form();
        form1.setId("form1");
        form1.setName("Formulario 1");
        form1.addTab(tab1_1);

        Forms.put(form1.getId(), form1);
    }

    public static Map<String, Form> getForms() {
        load();
        return Forms;
    }

}
