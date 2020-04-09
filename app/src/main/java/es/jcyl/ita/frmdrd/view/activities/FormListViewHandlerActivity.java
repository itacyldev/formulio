package es.jcyl.ita.frmdrd.view.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.actions.ActionType;
import es.jcyl.ita.frmdrd.actions.UserAction;
import es.jcyl.ita.frmdrd.actions.interceptors.ViewUserActionInterceptor;
import es.jcyl.ita.frmdrd.forms.FormListController;

public class FormListViewHandlerActivity extends AppCompatActivity {

    private FormListController formController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainController mc = MainController.getInstance();
        mc.getRouter().registerActivity(this);
        formController = (FormListController) mc.getFormController();

        setContentView(R.layout.activity_form_list_view_handler);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        Context context = this;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // FAB new entity button, navigate to form view without entityId
                ViewUserActionInterceptor userActionInterceptor = mc.getRenderingEnv().getUserActionInterceptor();
                if (userActionInterceptor != null) {
                    UserAction action = UserAction.NavigateAction(context, null,
                            formController.getEditRoute());
                    action.setOrigin(formController.getId());
                    userActionInterceptor.doAction(action);
                }
            }
        });

        View viewRoot = mc.renderView(this);
        // render the view in the content_form_list_view_handler
        ViewGroup bodyContent = findViewById(R.id.body_content);
        bodyContent.addView(viewRoot);
    }

    protected void close() {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainController mc = MainController.getInstance();
        mc.getActionController().doUserAction(new UserAction(this, null, ActionType.BACK));
        finish();
    }

}
