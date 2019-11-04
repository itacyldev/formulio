package es.jcyl.ita.frmdrd.lifecycle.phase;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.frmdrd.configuration.FormConfigHandler;
import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.context.impl.FormContext;
import es.jcyl.ita.frmdrd.render.FormRenderer;
import es.jcyl.ita.frmdrd.ui.form.UIField;
import es.jcyl.ita.frmdrd.ui.form.UIForm;

public class RenderViewPhase extends Phase {

    private Activity parentActivity;

    public RenderViewPhase() {
        this.id = PhaseId.RENDER_VIEW;
    }

    @Override
    public void execute(Context phaseContext) {
        FormRenderer renderer = new FormRenderer(lifecycle);


        // If we're updating the view only render the updated fields
        if (phaseContext != null) {
            FormContext formContext = (FormContext) lifecycle.getMainContext().getContext(
                    "form");
            List<UIField> updateFields = new ArrayList<>();
            for (Object idField : phaseContext.values()) {
                UIField field = formContext.getFieldConfig(idField);
                updateFields.add(field);
                String updateStr = field.getUpdate();
                updateFields.addAll(getFields(updateStr, formContext));
            }

            renderer.render(updateFields);

        } else {
            Context mainContext = lifecycle.getMainContext();
            this.parentActivity = (Activity) mainContext.get("lifecycle.activity");
            String formId = lifecycle.getFormId();
            UIForm form = FormConfigHandler.getForm(formId);
            renderer.render(parentActivity, form);
        }

    }

    private List<UIField> getFields(String fieldsStr, Context formContext) {
        List<UIField> fields = new ArrayList<>();
        return fields;
    }
}
