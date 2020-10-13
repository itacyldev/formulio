package es.jcyl.ita.formic.core.context;
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
import es.jcyl.ita.formic.forms.context.impl.FormContext;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FormContextHelper {

    public static void setMessage(FormContext context, String elementId, String message) {
        // TODO: more than one message
        Context msgCtx = context.getContext("messages");
        msgCtx.put(elementId, message);
    }

    public static String getMessage(FormContext context, String elementId) {
        // TODO: more than one message
        if (context == null || elementId == null) {
            return null;
        }
        Context msgCtx = context.getContext("messages");
        return (String) msgCtx.get(elementId);
    }
}