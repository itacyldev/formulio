package es.jcyl.ita.frmdrd.lifecycle.phase;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import es.jcyl.ita.frmdrd.context.Context;
import es.jcyl.ita.frmdrd.render.FormRenderer;
import es.jcyl.ita.frmdrd.ui.form.Form;

public class RenderViewPhase extends Phase {

    private Activity parentActivity;

    public RenderViewPhase() {
        this.id = PhaseId.RENDER_VIEW;
    }

    @Override
    public void execute(Context context) {
        this.parentActivity = (Activity) context.get("lifecycle.acticity");
        Form form = (Form) context.get("form");
        this.render(form);
    }


    private void render(Form form) {
        FormRenderer renderer = new FormRenderer();
        LinearLayout fieldsLayout = (LinearLayout) renderer.render(parentActivity, form);
        fieldsLayout.setVisibility(View.VISIBLE);
    }
}
