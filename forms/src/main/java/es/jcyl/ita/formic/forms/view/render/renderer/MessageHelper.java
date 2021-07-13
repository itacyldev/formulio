package es.jcyl.ita.formic.forms.view.render.renderer;
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

import java.util.List;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.UIGroupComponent;
import es.jcyl.ita.formic.forms.view.helpers.ViewHelper;
import es.jcyl.ita.formic.forms.view.widget.StatefulWidget;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;

/**
 * Provides methods for renderes and validators to access component error messages.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class MessageHelper {

    /*******************************/
    /*** Renderers message access **/
    /*******************************/
    /**
     * Gives access to renderers to check current component message errors
     *
     * @param env
     * @param component
     * @return
     */
    public static String getMessage(RenderingEnv env, UIComponent component) {
        WidgetContext widgetContext = env.getWidgetContext();
        return getMessage(widgetContext, component.getId());
    }

    public static void setMessage(RenderingEnv env, UIComponent component, String msg) {
        WidgetContext widgetContext = env.getWidgetContext();
        setMessage(widgetContext, component.getId(), msg);
    }

    public static void clearMessages(RenderingEnv env) {
        env.getMessageMap().clear();
    }

    public static void clearMessage(RenderingEnv env, UIComponent component) {
        WidgetContext widgetContext = env.getWidgetContext();
        clearMessage(widgetContext, component.getId());
    }


    /**
     * Checks if any of the nested elements of the GroupComponent has an error
     *
     * @param env
     * @param widget
     */
    public static boolean hasNestedMessages(RenderingEnv env, Widget widget) {
        if (widget instanceof WidgetContextHolder) {
            // get message context for this widget
            BasicContext msgCtx = env.getMessageContext(widget.getComponentId());
            return (msgCtx == null) ? false : !msgCtx.isEmpty();
        } else {
            // get all nested widgetContext holders and check if any of them
            // has not empty messageContext
            List<WidgetContextHolder> list = ViewHelper.findNestedWidgetsByClass(widget, WidgetContextHolder.class);

            for (WidgetContextHolder holder : list) {
                // find MessageContext and check if there area errors
                BasicContext msgCtx = env.getMessageContext(holder.getHolderId());
                if (msgCtx != null && !msgCtx.isEmpty()) {
                    return true;
                }
            }
            // Use upper WidgetContextHolder to find nested elements
            // try directly attached statefull elements using their
            String holderId = widget.getWidgetContext().getHolderId();
            BasicContext msgCtx = env.getMessageContext(holderId);
            if (msgCtx != null) {
                List<StatefulWidget> lstStatefull = ViewHelper.findNestedWidgetsByClass(widget, StatefulWidget.class);
                for (StatefulWidget stWidget : lstStatefull) {
                    if (msgCtx.containsKey(stWidget.getComponentId())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /********************************/
    /*** Validators message access **/
    /********************************/

    public static void setMessage(WidgetContext context, String elementId, String message) {
        BasicContext messageContext = context.getMessageContext();
        messageContext.put(elementId, message);
    }

    public static String getMessage(WidgetContext context, String elementId) {
        Context messageContext = context.getMessageContext();
        return (messageContext == null) ? null : (String) messageContext.get(elementId);
    }

    public static void clearMessage(WidgetContext context, String elementId) {
        BasicContext messageContext = context.getMessageContext();
        if (messageContext != null) {
            messageContext.remove(elementId);
        }
    }

    /**
     * Removes all messages from WidgetContext
     *
     * @param context
     */
    public static void clearMessages(WidgetContext context) {
        BasicContext messageContext = context.getMessageContext();
        if (messageContext != null) {
            messageContext.clear();
        }
    }

}