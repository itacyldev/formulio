package es.jcyl.ita.frmdrd.lifecycle.phase;

import android.app.Activity;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.context.impl.FormContext;
import es.jcyl.ita.frmdrd.ui.components.form.FormRenderer;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;

public class RenderViewPhase extends Phase {

    private Activity parentActivity;

    public RenderViewPhase() {
        this.id = PhaseId.RENDER_VIEW;
    }

    @Override
    public void execute(Context phaseContext) {

        // get view handler
        // get view id from context
        // build view ViewRoot (get view configuration, restores if it it's already read)
        // check view Change
        // viewHandler.render(view, context)


        FormRenderer renderer = new FormRenderer();
        Context mainContext = lifecycle.getMainContext();
        this.parentActivity = (Activity) mainContext.get("lifecycle.activity");



        // If we're updating the view only render the updated fields
        if (phaseContext != null) {
            FormContext formContext = (FormContext) lifecycle.getMainContext().getContext(
                    "form");
            List<UIField> updateFields = new ArrayList<>();
            for (Object idField : phaseContext.values()) {
                UIField field = formContext.getFieldConfig(idField);
                updateFields.add(field);

                String updateStr = field.getUpdate();
                if (StringUtils.isNotEmpty(updateStr)) {
                    updateFields.addAll(getFields(updateStr, formContext));
                }
            }

//            renderer.render(parentActivity, updateFields);

        } else {

//            String formId = lifecycle.getFormId();
//            UIForm form = FormConfigHandler.getForm(formId);
//            renderer.render(parentActivity, form);
        }

    }

    /**
     * @param fieldsStr
     * @param formContext
     * @return
     */
    private List<UIField> getFields(String fieldsStr, FormContext formContext) {
        UIField field = formContext.getFieldConfig(fieldsStr);
        List<UIField> fields = new ArrayList<>();
        fields.add(field);
        return fields;
    }

//    @Override
//    public void execute(Context phaseContext) {
//
//        // get view handler
//        // get view id from context
//        // build view ViewRoot (get view configuration, restores if it it's already read)
//        // check view Change
//        // viewHandler.render(view, context)
//
//
//        FormRenderer renderer = new FormRenderer(lifecycle);
//        Context mainContext = lifecycle.getMainContext();
//        this.parentActivity = (Activity) mainContext.get("lifecycle.activity");
//
//        // If we're updating the view only render the updated fields
//        if (phaseContext != null) {
//            FormContext formContext = (FormContext) lifecycle.getMainContext().getContext(
//                    "form");
//            List<UIField> updateFields = new ArrayList<>();
//            for (Object idField : phaseContext.values()) {
//                UIField field = formContext.getFieldConfig(idField);
//                updateFields.add(field);
//
//                String updateStr = field.getUpdate();
//                if (StringUtils.isNotEmpty(updateStr)) {
//                    updateFields.addAll(getFields(updateStr, formContext));
//                }
//            }
//
//            renderer.render(parentActivity, updateFields);
//
//        } else {
//
//            String formId = lifecycle.getFormId();
//            UIForm form = FormConfigHandler.getForm(formId);
//            renderer.render(parentActivity, form);
//        }
//
//    }
}
