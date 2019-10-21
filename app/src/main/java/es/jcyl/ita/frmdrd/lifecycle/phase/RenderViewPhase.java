package es.jcyl.ita.frmdrd.lifecycle.phase;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import es.jcyl.ita.frmdrd.configuration.FormConfigHandler;
import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.render.FormRenderer;
import es.jcyl.ita.frmdrd.ui.form.UIForm;

public class RenderViewPhase extends Phase {

    private Activity parentActivity;

    public RenderViewPhase() {
        this.id = PhaseId.RENDER_VIEW;
    }

    @Override
    public void execute(Context context) {
        this.parentActivity = (Activity) context.get("lifecycle.activity");
        String formId =  lifecycle.getFormId();
        UIForm UIForm = FormConfigHandler.getForm(formId);
        this.render(UIForm);
    }


    private void render(UIForm UIForm) {
        FormRenderer renderer = new FormRenderer(lifecycle);
        LinearLayout fieldsLayout = (LinearLayout) renderer.render(parentActivity, UIForm);
        fieldsLayout.setVisibility(View.VISIBLE);
    }
}
