package es.jcyl.ita.formic.forms.context;
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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.context.impl.UnPrefixedCompositeContext;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ContextUtils {


    public static CompositeContext combine(Context ctx1, Context ctx2) {
        CompositeContext combinedContext = new UnPrefixedCompositeContext();
        if (ctx1 == null || ctx2 == null) {
            throw new IllegalArgumentException(String.format("Both context must be not null: ctx1:[%s] ctx2:[%s]", ctx1, ctx2));
        }
        combinedContext.addContext(ctx1);
        combinedContext.addContext(ctx2);
        return combinedContext;
    }
}
