package es.jcyl.ita.formic.forms.view.activities;

import android.content.Context;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.lang3.ArrayUtils;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionType;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.events.Event;
import es.jcyl.ita.formic.forms.actions.events.UserEventInterceptor;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.controllers.UIParam;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;

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
        if (ArrayUtils.isEmpty(this.formController.getActions())) {
            fab.hide();
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserAction action;
                    UIAction uiAction = formController.getAction("add");
                    if (uiAction == null) {
                        // get the first one until we refactorize this (FORMIC-229)
                        uiAction = formController.getActions()[0];
                        action = new UserAction(uiAction, formController);
                        prepareActionParams(uiAction, action);
                    } else {
                        // default navigation
                        action = UserAction.navigate(uiAction.getRoute(), formController);
                    }
                    // FAB new entity button, navigate to form view without entityId
                    UserEventInterceptor interceptor = env.getUserActionInterceptor();
                    if (interceptor != null) {
                        // TODO: FORMIC-229 Terminar refactorización de acciones
                        Event event = new Event(Event.EventType.CLICK, null, action);
                        interceptor.notify(event);
                    }
                }
            });
        }
    }

    /**
     * // TODO: remove this after FORMIC-229 Terminar refactorización de acciones
     * @return
     */
    private void prepareActionParams(UIAction uiAction, UserAction action) {
        if (uiAction.hasParams()) {
            for (UIParam param : uiAction.getParams()) {
                Object value = JexlFormUtils.eval(this.formController.getMc().getGlobalContext(),
                        param.getValue());
                action.addParam(param.getName(),value);
            }
        }
    }

    protected void close() {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainController mc = MainController.getInstance();
        UserAction action = new UserAction(ActionType.BACK.name(), "back", this.formController);
        mc.getActionController().doUserAction(action);
        finish();
    }

}
