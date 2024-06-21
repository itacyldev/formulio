package es.jcyl.ita.formic.forms.utils;
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

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.ActionType;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.UserActionHelper;
import es.jcyl.ita.formic.forms.actions.events.Event;
import es.jcyl.ita.formic.forms.actions.events.UserEventInterceptor;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * Utility class to simulate form navigation in tests
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class DevFormNav {

    private final android.content.Context context;
    private final MainController mc;
    private final UserEventInterceptor eventInterceptor;

    public DevFormNav(android.content.Context context, MainController mc) {
        this.mc = mc;
        RenderingEnv renderingEnv = mc.getRenderingEnv();
        this.context = context;
        this.eventInterceptor = renderingEnv.getUserActionInterceptor();
    }

    public void nav(String formControllerId) {
        nav(formControllerId, null);
    }

    public void nav(String formControllerId, Map<String, Object> params) {
        UserAction navAction = UserActionHelper.navigate(formControllerId);
        if (params != null) {
            navAction.setParams(params);
        }
        mc.getRouter().navigate(context, navAction);
        // manually render the view
        mc.renderView(context);
    }

    public void navToEntity(String formControllerId, String entityId) {
        Map<String, Object> params = new HashMap<>();
        params.put("entityId", entityId);
        nav(formControllerId, params);
    }

    public void clickSave() {
        UserAction action = UserActionHelper.newAction(ActionType.SAVE);
        Event event = new Event(Event.EventType.CLICK, null, action);
        eventInterceptor.notify(event);
    }

    public void clickSave(String controller) {
        UserAction action = UserActionHelper.newAction(ActionType.SAVE);
        action.setController(controller);
        Event event = new Event(Event.EventType.CLICK, null, action);
        eventInterceptor.notify(event);
    }

    public ViewController getViewController() {
        return mc.getViewController();
    }
    public ViewWidget getRootWidget() {
        return mc.getViewController().getRootWidget();
    }

    public <T> T getWidget(String componentId, Class<T> widgetClass) {
        ViewController viewController = mc.getViewController();
        ViewWidget rootWidget = viewController.getRootWidget();
        Widget widget = ViewHelper.findComponentWidget(rootWidget, componentId);
        return (widget == null) ? null : (T) widget;
    }

}
