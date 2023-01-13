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

import androidx.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.NativeJavaObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.utils.MockingUtils;
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
     * Creates a scriptable fixture (list with maps) and use the filter() function to get the last 3 items of the scriptable
     * collection
     */
    @Test
    public void testFilter() {
        ScriptEngine engine = new ScriptEngine();

        ScriptableList<Map> lst = createFixtureMap(engine);
        // Running the method "size()" over all the elements in collection, the expected return is five 1's
        Object myResult = runFunction(engine, filterFndSource, "myfunction", new Object[]{lst});

        ScriptableList resultLst = (ScriptableList) ((NativeJavaObject) myResult).unwrap();
        Assert.assertEquals(2, resultLst.size()); // 3,4 expected
    }


    /**
     * Uses the ScriptViewHelper to link filter/apply methods on ViewHolder objects
     */
    @Test
    public void testFilterApply() {
        String source = " function myfunction(vh) { " +
                "var viewContexts = vh.viewHolders();" +
                "lst = viewContexts.filter(obj => obj.holderId.startsWith('myWgt'));" +
                "lst = lst.flatMap(obj => obj.widgetContext.viewContext.statefulWidgets);\n" +
                "lst.apply(o => o.setValue('valor'));\n " +
                "   return viewContexts;" +
                "}";

        ScriptEngine engine = new ScriptEngine();

        // Running the method "size()" over all the elements in collection, the expected return is five 1's
        RenderingEnv rendEnv = ScriptViewTestUtils.createViewMock("myWgt", engine);
        MainController mc = MockingUtils.mockMainController(null, ContextTestUtils.createGlobalContext());
        when(mc.getRenderingEnv()).thenReturn(rendEnv);
        ScriptViewHelper vh =  new ScriptViewHelper(mc);

        Object myResult = runFunction(engine, source, "myfunction", new Object[]{vh});

        ScriptableList resultLst = (ScriptableList) ((NativeJavaObject) myResult).unwrap();
        Assert.assertNotNull(resultLst);

        // check inputFields of the view has changed their values
        ViewContext viewContext = vh.viewContexts().get(0);
        // get the mock and verify the set method has been called
        InputWidget widgetMock = (InputWidget) viewContext.getStatefulWidgets().get(0);
        verify(widgetMock).setValue("valor");
    }

    /**
     * Creates a scriptable fixture (list with maps) and use the filter() function to get the last 3 items of the scriptable
     * collection
     */
    @Test
    public void testReduce() {
        String source = " function myfunction(collection) { " +
                "   out.println(collection); " +
                "   return collection.reduce( (acc,o) => acc + o.size() )" +
                "}";

        ScriptEngine engine = new ScriptEngine();

        ScriptableList<Map> lst = createFixtureMap(engine);
        // Running the method "size()" over all the elements in collection, the expected return is five 1's
        NativeJavaObject myResult = (NativeJavaObject) runFunction(engine, source, "myfunction", new Object[]{lst});

        Assert.assertEquals(5.0, myResult.unwrap()); // 3,4 expected
    }


    @Test
    public void testMap() {
        String source = " function myfunction(collection) { " +
                "   out.println(collection); " +
                "   return collection.map(o => o.keySet())" +
                "}";
        ScriptEngine engine = new ScriptEngine();

        ScriptableList<Map> lst = createFixtureMap(engine);
        // Running the method "size()" over all the elements in collection, the expected return is five 1's
        NativeJavaObject myResult = (NativeJavaObject) runFunction(engine, source, "myfunction", new Object[]{lst});

        Assert.assertEquals(5, ((List)myResult.unwrap()).size()); // 3,4 expected
    }


    @Test
    public void testFlatMap() {
        String source = " function myfunction(collection) { " +
                "   out.println(collection); " +
                "   return collection.flatMap(o => o.values())" +
                "}";
        ScriptEngine engine = new ScriptEngine();

        ScriptableList<Map> lst = createFixtureMap(engine);
        // Running the method "size()" over all the elements in collection, the expected return is five 1's
        NativeJavaObject myResult = (NativeJavaObject) runFunction(engine, source, "myfunction", new Object[]{lst});

        Assert.assertEquals(5, ((List)myResult.unwrap()).size()); // 3,4 expected
    }

    /**
     * Creates a scriptable fixture (list with maps) and use the filter() function to get the last 3 items of the scriptable
     * collection
     */
    @Test
    public void testReduceSum() {
        String source = " function myfunction(collection) { " +
                "   out.println(collection); " +
                "   return collection.map(o => o.size()).reduceSum()" +
                "}";
        ScriptEngine engine = new ScriptEngine();

        ScriptableList<Map> lst = createFixtureMap(engine);
        // Running the method "size()" over all the elements in collection, the expected return is five 1's
        Object myResult = runFunction(engine, source, "myfunction", new Object[]{lst});

        Assert.assertEquals(5.0, (float) myResult, 0.001); // 3,4 expected
    }

    /**
     * Creates a scriptable fixture (list with maps) and use the filter() function to get the last 3 items of the scriptable
     * collection
     */
    @Test
    public void testReduceCount() {
        String source = " function myfunction(collection) { " +
                "   out.println(collection); " +
                "   return collection.map(o => o.size()).reduceCount()" +
                "}";

        ScriptEngine engine = new ScriptEngine();

        ScriptableList<Map> lst = createFixtureMap(engine);
        // Running the method "size()" over all the elements in collection, the expected return is five 1's
        Object myResult = runFunction(engine, source, "myfunction", new Object[]{lst});

        Assert.assertEquals(5, (int) myResult, 0.001); // 3,4 expected
    }

    @NonNull
    private ScriptableList<Map> createFixtureMap(ScriptEngine engine) {
        ScriptableList<Map> lst = new ScriptableList<>(engine);
        for (int i = 0; i < 5; i++) {
            Map m = new HashMap();
            m.put("obj" + i, i);
            // obj.values().toArray()[0]  gives the map value
            lst.add(m);
        }
        return lst;
    }


    private Object runFunction(ScriptEngine engine, String source, String fncName, Object[] params) {
        Map<String, Object> props = new HashMap<>();
        props.put("out", System.out);
        engine.initEngine(props);
        engine.store("formTest", ScriptRef.createInlineScriptRef(source,""));
        engine.initScope("formTest");

        return engine.callFunction(fncName, params);
    }



}
