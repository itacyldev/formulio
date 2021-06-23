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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.OrderedCompositeContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;

import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ContextTestUtils {

    public static CompositeContext createGlobalContext() {
        CompositeContext ctx = new UnPrefixedCompositeContext();
        ctx.addContext(new BasicContext("messages"));
        ctx.addContext(new BasicContext("state"));
        return ctx;
    }

    public static WidgetContext createWidgetContext() {
        Widget widget = mock(Widget.class);
        return createWidgetContext(widget);

    }

    public static WidgetContext createWidgetContext(Widget widget) {
        WidgetContextHolder holder = new WidgetContextHolder() {
            WidgetContext ctx;

            @Override
            public String getHolderId() {
                return null;
            }

            @Override
            public Widget getWidget() {
                return widget;
            }

            @Override
            public WidgetContext getWidgetContext() {
                return ctx;
            }

            @Override
            public void setWidgetContext(WidgetContext context) {
                ctx = context;
            }
        };

        WidgetContext ctx = new WidgetContext(holder);
        // messages and state contexts
        ctx.addContext(createGlobalContext());
        return ctx;
    }

    public static CompositeContext createGlobalContextWithParam(String param, Object value) {
        return createGlobalContextWithParam(new String[]{param}, new Object[]{value});
    }

    public static CompositeContext createGlobalContextWithParam(String[] params, Object[] values) {
        CompositeContext ctx = new OrderedCompositeContext();
        BasicContext bc = new BasicContext("params");
        for (int i = 0; i < params.length; i++) {
            bc.put(params[i], values[i]);
        }
        ctx.addContext(bc);
        return ctx;
    }
}
