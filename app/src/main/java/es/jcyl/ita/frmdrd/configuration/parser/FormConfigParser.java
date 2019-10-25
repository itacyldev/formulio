package es.jcyl.ita.frmdrd.configuration.parser;

import es.jcyl.ita.frmdrd.configuration.FormConfigHandler;
import es.jcyl.ita.frmdrd.ui.form.UIForm;

public abstract class FormConfigParser {

    /**
     *
     * @param formConfigStr
     * @return the form's ID
     */
    public abstract String parseFormConfig(String formConfigStr);

    protected void loadConfig(UIForm form) {
        FormConfigHandler.addForm(form);
    }
}
