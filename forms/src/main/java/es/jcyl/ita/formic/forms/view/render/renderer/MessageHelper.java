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

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.UIGroupComponent;

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
     * @param root
     */
    public static boolean hasNestedMessages(RenderingEnv env, UIComponent root) {
        if (!(root instanceof UIGroupComponent)) {
            return (getMessage(env, root) != null);
        } else {
            boolean hasMessage = (getMessage(env, root) != null);
            if (hasMessage) {
                return true;
            }
            // check in children
            if (root.hasChildren()) {
                for (UIComponent kid : root.getChildren()) {
                    hasMessage |= hasNestedMessages(env, kid);
                }
            }
            return hasMessage;
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