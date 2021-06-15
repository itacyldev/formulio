package es.jcyl.ita.formic.forms.view.widget;
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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.UIGroupComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class WidgetContextHelper {

    public static void setMessage(CompositeContext context, String elementId, String message) {
        // TODO: more than one message
        Context msgCtx = context.getContext("messages");
        msgCtx.put(elementId, message);
    }

    public static void clearMessage(CompositeContext context, String elementId) {
        Context msgCtx = context.getContext("messages");
        msgCtx.remove(elementId);
    }

    public static void clearMessages(CompositeContext context) {
        Context msgCtx = context.getContext("messages");
        msgCtx.clear();
    }

    public static String getMessage(CompositeContext context, String elementId) {
        Context msgCtx = context.getContext("messages");
        return (String) msgCtx.get(elementId);
    }

    /**
     * Checks if any of the nested elements of the GroupComponent has an error
     *
     * @param context
     * @param root
     */
    public static boolean hasNestedMessages(CompositeContext context, UIComponent root) {
        if (!(root instanceof UIGroupComponent)) {
            return (getMessage(context, root.getId()) != null);
        } else {
            boolean hasMessage = (getMessage(context, root.getId()) != null);
            if (hasMessage) {
                return true;
            }
            // check in children
            if (root.hasChildren()) {
                for (UIComponent kid : root.getChildren()) {
                    hasMessage |= hasNestedMessages(context, kid);
                }
            }
            return hasMessage;
        }
    }

}