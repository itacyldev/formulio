package es.jcyl.ita.formic.forms.view.activities;

import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionType;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.UserActionHelper;
import es.jcyl.ita.formic.forms.actions.events.Event;
import es.jcyl.ita.formic.forms.actions.events.UserEventInterceptor;
import es.jcyl.ita.formic.forms.components.buttonbar.UIButtonBar;
import es.jcyl.ita.formic.forms.components.link.UIButton;
import es.jcyl.ita.formic.forms.components.view.UIView;
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
    protected void createView(RenderingEnv renderingEnv) {
        // action buttons
        setTitle(viewController.getName());
        setToolbar(viewController.getName());
    }

    @Override
    protected void createToolBars(RenderingEnv renderingEnv) {
        UIView view = this.viewController.getView();
        createFabBar(view.getFabBar());
    }

    private void createFabBar(UIButtonBar buttonBar) {
        FloatingActionButton fab = findViewById(R.id.fab);
        if (buttonBar == null || !buttonBar.hasChildren()) {
            fab.hide();
            return;
        }
        // TODO: multi button fab-bar
        UIButton fabButton = (UIButton) buttonBar.getChildren()[0];
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAction action;
                UIAction uiAction = fabButton.getAction();
                if (uiAction != null) {
                    // get the first one until we refactorize this (FORMIC-229)
                    action = UserActionHelper.newAction(uiAction, viewController);
                } else {
                    // no action defined, use route from button and navigate
                    action = UserActionHelper.navigate(fabButton.getRoute(), viewController);
                }
                prepareActionParams(uiAction, action);
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

    /**
     * // TODO: remove this after FORMIC-229 Terminar refactorización de acciones
     *
     * @return
     */
    private void prepareActionParams(UIAction uiAction, UserAction action) {
        if (uiAction.hasParams()) {
            for (UIParam param : uiAction.getParams()) {
                Object value = JexlFormUtils.eval(this.viewController.getMc().getGlobalContext(),
                        param.getValue());
                action.addParam(param.getName(), value);
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
        UserAction action = UserActionHelper.newAction(ActionType.BACK.name(), "back", this.viewController);
        mc.getActionController().execAction(action);
        finish();
    }

}
