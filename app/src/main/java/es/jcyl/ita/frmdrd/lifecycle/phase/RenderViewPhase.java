package es.jcyl.ita.frmdrd.lifecycle.phase;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.frmdrd.configuration.FormConfigHandler;
import es.jcyl.ita.frmdrd.context.Context;
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
        // If we're updating the view only render the updated fields
        if (phaseContext != null) {
            Context formContext = lifecycle.getMainContext().getContext("form");

            List<UIField> updateFields = new ArrayList<>();
            for (String idField : phaseContext.keySet()) {
                UIField field = (UIField) formContext.get(idField);
                updateFields.add(field);
                String updateStr = field.getUpdate();
                updateFields.addAll(getFields(updateStr, formContext));
            }

            render(updateFields);
        } else {
            Context mainContext = lifecycle.getMainContext();
            this.parentActivity = (Activity) mainContext.get("lifecycle.activity");
            String formId = lifecycle.getFormId();
            UIForm UIForm = FormConfigHandler.getForm(formId);
            this.render(UIForm);
        }

    }


    private void render(UIForm form) {
        FormRenderer renderer = new FormRenderer(lifecycle);
        LinearLayout fieldsLayout =
                (LinearLayout) renderer.render(parentActivity, form);
        fieldsLayout.setVisibility(View.VISIBLE);
    }

    private void render(List<UIField> fields) {

    }

    private List<UIField> getFields(String fieldsStr, Context formContext) {
        List<UIField> fields = new ArrayList<>();
        return fields;
    }
}
