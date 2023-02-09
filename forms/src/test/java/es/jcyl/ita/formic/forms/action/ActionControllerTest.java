package es.jcyl.ita.formic.forms.action;
/*
 *
 *  * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.ActionContext;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.actions.ActionHandler;
import es.jcyl.ita.formic.forms.actions.ActionType;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.UserActionHelper;
import es.jcyl.ita.formic.forms.actions.UserCompositeAction;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.controllers.widget.WidgetController;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.forms.utils.MockingUtils;
import es.jcyl.ita.formic.forms.validation.ValidatorException;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
@Ignore
public class ActionControllerTest {

    @BeforeClass
    public static void setUp() {
        App.init(mock(Context.class), "");
    }

    @Test
    public void testRunBasicAction() {
        MainController mockMc = MockingUtils.mockMainController();
        Router router = new Router(mockMc);
        ActionController controller = new ActionController(mockMc, router);
        String myExpectedFormId = "myformId";
        UserAction action = UserActionHelper.navigate(myExpectedFormId);

        // Act
        controller.execAction(action);

        // assert
        assertEquals(myExpectedFormId, router.getCurrent().getFormId());
        assertEquals(action, controller.getCurrentAction());
    }

    @Test
    public void testRunCompositeAction() {
        // mock mainController and View Controller to provide a widget to run data modification actions
        MainController mockMc = MockingUtils.mockMainController();
        Router router = new Router(mockMc);
        // mock view controller
        WidgetController widgetController = mock(WidgetController.class);
        when(widgetController.save()).thenReturn(true);
        // mock principal widgetController
        ViewController ctrl = mock(ViewController.class);
        when(mockMc.getViewController()).thenReturn(ctrl);
        when(ctrl.getMainWidgetController()).thenReturn(widgetController);

        // Arrange controller
        ActionController controller = new ActionController(mockMc, router);

        String myExpectedFormId = "form2";
        UserAction lastAction = UserActionHelper.navigate(myExpectedFormId);
        UserAction[] subActions = new UserAction[]{UserActionHelper.newAction(ActionType.SAVE), UserActionHelper.newAction(ActionType.NAV),
                UserActionHelper.newAction(ActionType.DELETE), lastAction};
        UserCompositeAction compositeAction = new UserCompositeAction(subActions);

        // Act
        controller.execAction(compositeAction);

        // assert
        assertEquals(myExpectedFormId, router.getCurrent().getFormId());
        assertEquals(lastAction, controller.getCurrentAction());
    }

    /**
     * Runs an actionSequence with one ActionHandler that forces a ValidationError. Checks the
     * sequence has been aborted.
     */
    @Test
    public void testRunCompositeActionWithValidationError() {
        // mock mainController and View Controller to provide a widget to run data modification actions
        MainController mockMc = MockingUtils.mockMainController();
        Router router = new Router(mockMc);
        // mock view controller
        WidgetController widgetController = mock(WidgetController.class);
        when(widgetController.save()).thenReturn(true);
        // mock principal widgetController
        ViewController ctrl = mock(ViewController.class);
        when(mockMc.getViewController()).thenReturn(ctrl);
        when(ctrl.getMainWidgetController()).thenReturn(widgetController);

        // Arrange controller
        ActionController controller = new ActionController(mockMc, router);
        controller.register("mockError", new ValidationErrorActionHandler());

        String myExpectedFormId = "form2";
        UserAction lastAction = UserActionHelper.navigate(myExpectedFormId);
        UserAction mockErrorAction = UserActionHelper.newAction("mockError");
        UserAction[] subActions = new UserAction[]{UserActionHelper.newAction(ActionType.SAVE), mockErrorAction,
                UserActionHelper.newAction(ActionType.NAV),
                UserActionHelper.newAction(ActionType.DELETE), lastAction};
        UserCompositeAction compositeAction = new UserCompositeAction(subActions);

        // Act
        controller.execAction(compositeAction);

        // assert
        assertEquals(mockErrorAction, controller.getCurrentAction());
    }


    public class ValidationErrorActionHandler implements ActionHandler {

        @Override
        public void handle(ActionContext actionContext, UserAction action) {
            throw new ValidatorException("Forced error");
        }

        @Override
        public UserAction prepareNavigation(ActionContext actionContext, UserAction action) {
            return null;
        }

        @Override
        public void onError(ActionContext actionContext, UserAction action, Exception e) {

        }

        @Override
        public String getSuccessMessage(ActionContext actionContext, UserAction action) {
            return null;
        }
    }
}
