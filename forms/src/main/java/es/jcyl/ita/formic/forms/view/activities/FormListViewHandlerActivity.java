package es.jcyl.ita.formic.forms.view.activities;

import android.content.Context;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.events.Event;
import es.jcyl.ita.formic.forms.actions.events.UserEventInterceptor;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;

public class FormListViewHandlerActivity extends BaseFormActivity<FormListController>
        implements FormActivity<FormListController> {


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_form_list_view_handler;
    }

    @Override
    protected void doRender(RenderingEnv renderingEnv) {
        // action buttons
        setTitle(formController.getName());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        renderFAB(renderingEnv);
    }


    private void renderFAB(RenderingEnv renderingEnv) {
        FloatingActionButton fab = findViewById(R.id.fab);
        if (!this.formController.hasAction("add")) {
            fab.hide();
        } else {
            Context context = this;
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // FAB new entity button, navigate to form view without entityId
                    UserEventInterceptor interceptor = env.getUserActionInterceptor();
                    if (interceptor != null) {
                        UserAction action = UserAction.navigate(formController.getAction("add").getRoute(), formController);
                        // TODO: FORMIC-229 Terminar refactorización de acciones
                        Event event = new Event(Event.EventType.CLICK, null, action);
                        interceptor.notify(event);
                    }
                }
            });
        }
    }

    protected void close() {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainController mc = MainController.getInstance();
        mc.getActionController().doUserAction(UserAction.back(this.formController));
        finish();
    }

}
