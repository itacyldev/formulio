package es.jcyl.ita.frmdrd.view.activities;

import android.content.Context;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.forms.FormListController;

public class FormListViewHandlerActivity extends BaseFormActivity<FormListController>
        implements FormActivity<FormListController> {


    /**
     * View element used to render the forms defined for this controller
     */
//    private ViewGroup contentView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_form_list_view_handler);
//        contentView = this.findViewById(R.id.body_content);
//
//        MainController mc = MainController.getInstance();
//        mc.registerActivity(this);
//
//        // render edit view content and link content view
//        View viewRoot = mc.renderView(this);
//        contentView.addView(viewRoot);
//
//        // check if there are messages to show
//        UserMessagesHelper.showGlobalMessages(this, mc.getRouter());
//    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_form_list_view_handler;
    }

    @Override
    protected void doRender() {
        // action buttons
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        renderFAB();
    }


    private void renderFAB() {
        FloatingActionButton fab = findViewById(R.id.fab);
        if (!this.formController.hasAction("add")) {
            fab.hide();
        } else {
            Context context = this;
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // FAB new entity button, navigate to form view without entityId

                    ViewUserActionInterceptor userActionInterceptor = env.getUserActionInterceptor();
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

}
