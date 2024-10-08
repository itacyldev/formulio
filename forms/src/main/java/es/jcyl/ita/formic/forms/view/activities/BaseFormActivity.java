package es.jcyl.ita.formic.forms.view.activities;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public abstract class BaseFormActivity<F extends ViewController> extends BaseActivity
        implements FormActivity<F> {

    protected Router router;
    protected RenderingEnv env;
    protected F viewController;
    private ProgressBarHelper progressBarHelper;
    /**
     * View element used to render the forms defined for this controller
     */
    private ViewGroup contentView;

    protected void doOnCreate() {
        progressBarHelper = new ProgressBarHelper(this);
        progressBarHelper.show();

        MainController mc = MainController.getInstance();
        attachContentView();
        mc.registerActivity(this);
        ActivityCallback callable = new ActivityCallback(this, mc, this.viewController, this.contentView);
        mc.renderViewAsync(this, callable);
    }

    public class ActivityCallback {
        private final ViewController viewController;
        private final MainController mc;
        private final ViewGroup contentView;
        private final Activity activity;

        public ActivityCallback(Activity activity, MainController mc, ViewController viewController, ViewGroup contentView) {
            this.activity = activity;
            this.contentView = contentView;
            this.mc = mc;
            this.viewController = viewController;
        }

        public void call(View view) throws Exception {
            try {
                contentView.addView(view);
                contentView.setFocusable(false);
            } catch (Exception e) {
                DevConsole.error("Error trying to render view " + this.viewController.getId(), e);
                router.back(activity, new String[]{"Sorry, there was an error while trying to render the view. " +
                        "See console for details."});
            }
            createView(mc.getRenderingEnv());
            createToolBars(mc.getRenderingEnv());
            progressBarHelper.hide();
        }
    }

    protected abstract void createToolBars(RenderingEnv renderingEnv);


    protected void attachContentView() {
        int layoutId = getLayoutResource();
        setContentView(layoutId);
        contentView = this.findViewById(R.id.body_content);
    }

    protected abstract int getLayoutResource();

    protected abstract void createView(RenderingEnv renderingEnv);

    protected void showMessages() {
        // check if there are messages to show
        UserMessagesHelper.showGlobalMessages(this, router);
    }

    @Override
    public void setViewController(F viewController) {
        this.viewController = viewController;
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

    public void registerCallBackForActivity(ActivityResultCallBack callback) {
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(callback.getContract(),
                callback.getCallBack()
        );
        callback.setResultLauncher(this, mGetContent);
    }
}
