package es.jcyl.ita.frmdrd.lifecycle.phase;


import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.frmdrd.configuration.FormConfigHandler;
import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.context.impl.FormContext;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;

public class BuildFormContextPhase extends Phase {

    public BuildFormContextPhase() {
        this.id = PhaseId.BUILD_FORM_CONTEXT;
    }

    /**
     * @param context
     */
    @Override
    public void execute(Context context) {
        UIView view = FormConfigHandler.getView(lifecycle.getFormId());
//        Context formContext = buildContext(form);

//        lifecycle.addContext(formContext);
    }

//    /**
//     * @param form
//     * @return
//     */
//    private Context buildContext(UIForm form) {
//        FormContext formContext = new FormContext("form", form);
//
//        List<UIField> fields = new ArrayList<>();
//        getFields(form, fields);
//        for (UIField field : fields) {
//            formContext.put(field.getId(), field);
//        }
//
//        return formContext;
//    }

    /**
     * @param component
     * @param fields
     */
    private void getFields(UIComponent component, List<UIField> fields) {
        if (component instanceof UIField) {
            fields.add((UIField) component);
        } else {
            for (UIComponent child : component.getChildren()) {
                getFields(child, fields);
            }
        }

    }

}
