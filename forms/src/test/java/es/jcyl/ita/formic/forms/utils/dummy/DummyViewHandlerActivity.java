package es.jcyl.ita.formic.forms.utils.dummy;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JxltEngine;
import org.apache.commons.lang3.ArrayUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import java.io.Serializable;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionType;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.UserActionHelper;
import es.jcyl.ita.formic.forms.actions.events.Event;
import es.jcyl.ita.formic.forms.actions.events.UserEventInterceptor;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.buttonbar.UIButtonBar;
import es.jcyl.ita.formic.forms.components.link.UIButton;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.controllers.UIParam;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;
import es.jcyl.ita.formic.forms.view.activities.BaseFormActivity;
import es.jcyl.ita.formic.forms.view.activities.FormActivity;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/*
 * Copyright 2020 Gustavo Río Briones (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
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

/**
 * @author Gustavo Río Briones (gustavo.rio@itacyl.es)
 */

public class DummyViewHandlerActivity extends BaseFormActivity<ViewController>
        implements FormActivity<ViewController> {
    @Override
    protected void doOnCreate() {
        // do nothing
    }

    @Override
    protected void doRender(RenderingEnv renderingEnv) {
    }

    protected void renderToolBars(RenderingEnv env) {
    }

    @Override
    protected int getLayoutResource() {
        return 0;
    }

    protected void close() {
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}