package es.jcyl.ita.formic.forms.view.async;
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

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.view.activities.BaseFormActivity;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class AsyncRenderWorker implements Runnable {
    private final MainController mc;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    Handler handler = new Handler(Looper.getMainLooper());
    private UIView uiView;
    private MainController.PostRenderCallBack callback;

    public AsyncRenderWorker(MainController mc) {
        this.mc = mc;
    }

    public void runRender(UIView uiView, MainController.PostRenderCallBack callback) {
        this.uiView = uiView;
        this.callback = callback;
        executor.execute(this);
    }

    @Override
    public void run() {
        // Background work
        Looper.prepare();

        ViewController viewController = mc.getViewController();
        mc.getScriptEngine().initContext();
        mc.getScriptEngine().initScope(viewController.getId());
        viewController.onBeforeRender();

        Widget widget = mc.getViewRenderer().render(mc.getRenderingEnv(), uiView);

        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.call(widget);
            }
        });
    }
}
