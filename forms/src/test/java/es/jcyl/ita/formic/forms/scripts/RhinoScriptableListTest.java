package es.jcyl.ita.formic.forms.scripts;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.NativeJavaObject;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.utils.dummy.DummyWidgetContextHolder;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.InputWidget;
import es.jcyl.ita.formic.forms.view.widget.StatefulWidget;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class RhinoScriptableListTest {
    private static final String RHINO_LOG = "var log = Packages.io.vec.ScriptAPI.log;";

    private String applyLambdaSource = "obj => obj.size()";
    private String applyFndSource = String.format(
            " function myfunction(collection) { " +
                    "   out.println(collection); " +
                    "   return collection.apply(%s)" +
                    "}", applyLambdaSource);

    /**
     * Create an scriptableList with one-item maps and use apply to obtain the size of each map
     */
    @Test
    public void testApply() {
        ScriptEngine engine = new ScriptEngine();

        ScriptableList<Map> lst = new ScriptableList<>(engine);
        for (int i = 0; i < 5; i++) {
            Map m = new HashMap();
            m.put("obj" + i, RandomUtils.randomString(5));
            lst.add(m);
        }
        // Running the method "size()" over all the elements in collection, the expected return is five 1's
        Object myResult = runFunction(engine, applyFndSource, "myfunction", new Object[]{lst});
        ScriptableList resultLst = (ScriptableList) ((NativeJavaObject) myResult).unwrap();
        Assert.assertEquals(5, resultLst.size());
        Assert.assertEquals(1, resultLst.get(0));
    }

    private String filterLambdaSource = "obj => obj.values().toArray()[0] > 2";
    private String filterFndSource = String.format(
            " function myfunction(collection) { " +
                    "   out.println(collection); " +
                    "   return collection.filter(%s)" +
                    "}", filterLambdaSource);

    /**
     * Creates a scriptable list with maps and use the filter() function to get the last 3 items of the scriptable
     * collection
     */
    @Test
    public void testFilter() {
        ScriptEngine engine = new ScriptEngine();

        ScriptableList<Map> lst = new ScriptableList<>(engine);
        for (int i = 0; i < 5; i++) {
            Map m = new HashMap();
            m.put("obj" + i, i);
            // obj.values().toArray()[0]  gives the map value
            lst.add(m);
        }
        // Running the method "size()" over all the elements in collection, the expected return is five 1's
        Object myResult = runFunction(engine, filterFndSource, "myfunction", new Object[]{lst});

        ScriptableList resultLst = (ScriptableList) ((NativeJavaObject) myResult).unwrap();
        Assert.assertEquals(2, resultLst.size()); // 3,4 expected
    }

    private String test3FndSource = " function myfunction(vh) { " +
            "var viewContexts = vh.viewHolders();" +
            "lst = viewContexts.filter(obj => obj.holderId.startsWith('myWgt'));" +
            "lst = lst.apply(obj => obj.widgetContext.viewContext);\n" +
            "lst.apply(obj => obj.set('myProperty','valor'));\n " +
            "   return viewContexts;" +
            "}";

    /**
     * Uses the ScriptViewHelper to link filter/apply methods on ViewHolder objects
     */
    @Test
    public void testFilterApply() {
        ScriptEngine engine = new ScriptEngine();

        // Running the method "size()" over all the elements in collection, the expected return is five 1's
        ScriptViewHelper vh = createViewMock("myWgt", engine);

        Object myResult = runFunction(engine, test3FndSource, "myfunction", new Object[]{vh});

        ScriptableList resultLst = (ScriptableList) ((NativeJavaObject) myResult).unwrap();
        Assert.assertNotNull(resultLst);

        // check inputFields of the view has changed their values
        ViewContext viewContext = vh.viewContexts().get(0);
        // get the mock and verify the set method has been called
        InputWidget widgetMock = (InputWidget) viewContext.getStatefulWidgets().get(0);
        verify(widgetMock).setValue("valor");
    }


    private Object runFunction(ScriptEngine engine, String source, String fncName, Object[] params) {
        Map<String, Object> props = new HashMap<>();
        props.put("out", System.out);
        engine.initEngine(props);
        engine.store("formTest", source);
        engine.initScope("formTest");

        return engine.callFunction(fncName, params);
    }

    private ScriptViewHelper createViewMock(String prefix, ScriptEngine engine) {
        // create 5 widgetContext, each context has a widget it its View context with Id "myproperty"
        Map<String, WidgetContextHolder> map = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            String id = prefix + "-" + i;
            WidgetContextHolder wCtx = new DummyWidgetContextHolder(id);
            InputWidget widget = mock(InputWidget.class);
            UIInputComponent component = mock(UIInputComponent.class);
            when(component.getId()).thenReturn("myProperty");
            when(widget.getComponent()).thenReturn(component);
            wCtx.getWidgetContext().getViewContext().registerWidget(widget);
            map.put(id, wCtx);
        }
        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(mcAC);
        env.setScriptEngine(engine);
        // set the root view Widget
        ViewWidget viewWidget = mock(ViewWidget.class);
        when(viewWidget.getContextHoldersMap()).thenReturn(map);
        when(viewWidget.getContextHolders()).thenReturn(map.values());
        env.setRootWidget(viewWidget);
        return new ScriptViewHelper(env, ContextTestUtils.createGlobalContext());
    }

}
