package es.jcyl.ita.frmdrd.view.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.forms.FormListController;
import es.jcyl.ita.frmdrd.router.Router;
import es.jcyl.ita.frmdrd.view.UserMessagesHelper;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

public class FormListViewHandlerActivity extends AppCompatActivity implements FormActivity<FormListController> {

    private Router router;
    private RenderingEnv env;
    private FormListController formController;
    /**
     * View element used to render the forms defined for this controller
     */
    private ViewGroup contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list_view_handler);
        contentView = this.findViewById(R.id.body_content);

        MainController mc = MainController.getInstance();
        mc.registerActivity(this);

        // render edit view content and link content view
        View viewRoot = mc.renderView(this);
        contentView.addView(viewRoot);

        // action buttons
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        renderFAB(mc);

        // check if there are messages to show
        UserMessagesHelper.showGlobalMessages(this, mc.getRouter());
    }

    private void renderFAB(MainController mc) {
        FloatingActionButton fab = findViewById(R.id.fab);
        if (!this.formController.hasAction("add")) {
            fab.hide();
        } else {
            Context context = this;
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // FAB new entity button, navigate to form view without entityId
                    ViewUserActionInterceptor userActionInterceptor = mc.getRenderingEnv().getUserActionInterceptor();
                    if (userActionInterceptor != null) {
                        UserAction action = UserAction.navigate(context, null,
                                formController.getAction("add").getRoute());
                        action.setOrigin(formController.getId());
                        userActionInterceptor.doAction(action);
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
        mc.getActionController().doUserAction(UserAction.back(this));
        finish();
    }


    @Override
    public void setFormController(FormListController formController) {
        this.formController = formController;
    }


    @Override
    public void setRouter(Router router) {
        this.router = router;
    }

    @Override
    public void setRenderingEnv(RenderingEnv env) {
        this.env = env;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public ViewGroup getContentView() {
        return contentView;
    }

}
