package es.jcyl.ita.formic.forms.action;
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

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.ActionContext;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.handlers.EntityChangeAction;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.utils.MockingUtils;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;

import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class ChangeEntityActionHandlerTest {
    static Context ctx;

    @Test
    public void testForceRefresh() throws Exception {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();

        // mock main controller and prepare action controller
        MainController mc = MockingUtils.mockMainController(ctx);
        ActionController actionController = new ActionController(mc, mc.getRouter());

        EntityChangeAction<FormEditController> actionHandler = createEmptyAction(mc);
        actionController.register("testAction", actionHandler);

        // prepare user Action
        UserAction userAction = new UserAction("testAction", mock(UIComponent.class));
        userAction.setForceRefresh(true);
        // act
        actionController.doUserAction(userAction);
        // assert
        verify(mc).renderBack();

    }

    @Test
    public void testBackNavigation() throws Exception {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();

        // mock main controller and prepare action controller
        MainController mc = MockingUtils.mockMainController(ctx);
        ActionController actionController = new ActionController(mc, mc.getRouter());

        EntityChangeAction<FormEditController> actionHandler = createEmptyAction(mc);
        actionController.register("testAction", actionHandler);

        // prepare user Action
        UserAction userAction = new UserAction("testAction", mock(UIComponent.class));
        userAction.setRoute("back");
        // act
        actionController.doUserAction(userAction);
        //assert
        verify(mc.getRouter()).navigate(any(ActionContext.class), any(), any());
    }


    @NotNull
    private EntityChangeAction<FormEditController> createEmptyAction(MainController mc) {
        return new EntityChangeAction<FormEditController>(mc, mc.getRouter()) {
            @Override
            protected void doAction(ActionContext actionContext, UserAction action) {
            }

            @Override
            protected String getSuccessMessage() {
                return null;
            }

            @Override
            protected String getErrorMessage(Exception e) {
                return null;
            }
        };
    }
}
