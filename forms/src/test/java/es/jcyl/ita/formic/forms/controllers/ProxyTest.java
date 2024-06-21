package es.jcyl.ita.formic.forms.controllers;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.components.inputfield.UIField;
import es.jcyl.ita.formic.forms.config.builders.proxy.UIComponentProxyFactory;
import es.jcyl.ita.formic.forms.config.meta.Attribute;
import es.jcyl.ita.formic.forms.config.meta.AttributeDef;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.view.widget.WidgetContext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class ProxyTest {
    ValueExpressionFactory expressionFactory = ValueExpressionFactory.getInstance();
//
//    private InvocationHandler buildInvocationHandler() {
//        InvocationHandler handler = new InvocationHandler() {
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                if (method.getName().equals("nextInt")) {
//                    // Chosen by fair dice roll, guaranteed to be random.
//                    return 4;
//                }
//                Object result = ProxyBuilder.callSuper(proxy, method, args);
//                System.out.println("Method: " + method.getName() + " args: "
//                        + Arrays.toString(args) + " result: " + result);
//                return result;
//            }
//        };
//        return handler;
//    }
//
//    @Test
//    @Ignore("tests with dexmaker")
//    public void testCreateProxy() throws IOException {
//        String path = InstrumentationRegistry.getInstrumentation().getTargetContext().getCacheDir().getPath();
//        System.setProperty("dexmaker.dexcache", path);
//        path = new File("/tmp").getPath();
//
//        InvocationHandler handler = buildInvocationHandler();
//
//        Random debugRandom = ProxyBuilder.forClass(Random.class)
//                .dexCache(new File(path))
//                .parentClassLoader(this.getClass().getClassLoader())
//                .handler(handler)
//                .build();
//    }

    @Test
    public void testCreateUIFieldProxy() throws InstantiationException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException {

        String expectedHint = RandomStringUtils.randomAlphanumeric(10);
        BasicContext ctx = new BasicContext("test");
        ctx.put("valor", expectedHint);

        UIField uiDelegate = new UIField();
        uiDelegate.setValueExpression(expressionFactory.create("${entity.property}"));
        String expectedPattern = "pattern-abcde";
        uiDelegate.setPattern(expectedPattern);

        WidgetContext widgetContext = ContextTestUtils.createWidgetContext();
        widgetContext.addContext(ctx);

        UIComponentProxyFactory factory = UIComponentProxyFactory.getInstance();
        factory.setWidgetContext(widgetContext);

        UIField proxy = (UIField) factory.create(uiDelegate,
                new String[]{"gethint", "isrendered"},
                new Attribute[]{AttributeDef.HINT, AttributeDef.RENDER},
                new ValueBindingExpression[]{expressionFactory.create("${test.valor}"),
                        expressionFactory.create("${not(empty(test.valor))}")});

        Assert.assertEquals(expectedHint, proxy.getHint());
        Assert.assertEquals(true, proxy.isRendered(null));
        Assert.assertEquals(expectedPattern, proxy.getPattern());
        Assert.assertEquals(3, proxy.getValueBindingExpressions().size());
    }


    @Test
    public void testCreateProxyBBuddy() throws InstantiationException, IllegalAccessException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String path = appContext.getCacheDir().getPath();

        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello World!"))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();
        assertThat(dynamicType.newInstance().toString(), is("Hello World!"));
    }
}