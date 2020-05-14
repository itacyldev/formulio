package es.jcyl.ita.frmdrd.view.activities;
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

import es.jcyl.ita.frmdrd.BaseActivity;
import es.jcyl.ita.frmdrd.MainController;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.config.DevConsole;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.router.Router;
import es.jcyl.ita.frmdrd.view.UserMessagesHelper;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public abstract class BaseFormActivity<F extends FormController> extends BaseActivity
        implements FormActivity<F> {

    protected Router router;
    protected RenderingEnv env;
    protected F formController;
    /**
     * View element used to render the forms defined for this controller
     */
    private ViewGroup contentView;

    protected void doOnCreate() {
        attachContentView();

        MainController mc = MainController.getInstance();
        mc.registerActivity(this);

        // render edit view content and link content view
        try {
            View viewRoot = mc.renderView(this);
            contentView.addView(viewRoot);
        }catch(Exception e){
            DevConsole.error("Error trying to render view " + this.formController.getId(),e);
            router.back(this, new String[] {"Sorry, there was an error trying to render the view. " +
                    "See console for details."});
        }

        doRender();
        showMessages();
    }

    protected void attachContentView() {
        int layoutId = getLayoutResource();
        setContentView(layoutId);
        contentView = this.findViewById(R.id.body_content);
    }

    protected abstract int getLayoutResource();

    protected abstract void doRender();

    protected void showMessages() {
        // check if there are messages to show
        UserMessagesHelper.showGlobalMessages(this, router);
    }

    @Override
    public void setFormController(F formController) {
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
