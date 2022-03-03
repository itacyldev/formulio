package es.jcyl.ita.formic.forms;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.android.AndroidClassLoadingStrategy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.inputfield.UIField;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.builders.proxy.UIComponentInvocationHandler;
import es.jcyl.ita.formic.forms.config.builders.proxy.UIComponentProxyFactory;
import es.jcyl.ita.formic.forms.config.meta.Attribute;
import es.jcyl.ita.formic.forms.config.meta.AttributeDef;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;

@RunWith(AndroidJUnit4.class)
public class ProxyInstrumentationTest {
    ValueExpressionFactory expressionFactory = ValueExpressionFactory.getInstance();


    @BeforeClass
    public static void init() {
        System.setProperty("formic.classLoading", "android");
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String path = appContext.getCacheDir().getPath();
        System.setProperty("formic.classCache", path);

        App.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    @Test
    public void testComponent() throws InstantiationException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException {

        String expectedHint = RandomStringUtils.randomAlphanumeric(10);
        BasicContext ctx = new BasicContext("test");
        ctx.put("valor", expectedHint);

        UIField uiDelegate = new UIField();
        uiDelegate.setValueExpression(expressionFactory.create("${entity.property}"));
        String expectedPattern = "pattern-abcde";
        uiDelegate.setPattern(expectedPattern);

        WidgetContext widgetContext = new WidgetContext();
        widgetContext.addContext(ctx);

        UIComponentProxyFactory factory = UIComponentProxyFactory.getInstance();
        factory.setWidgetContext(widgetContext);

        UIField proxy = (UIField)factory.create(uiDelegate,
                new String[]{"gethint", "isrendered"},
                new Attribute[]{AttributeDef.HINT, AttributeDef.RENDER},
                new ValueBindingExpression[]{expressionFactory.create("${test.valor}"),
                        expressionFactory.create("${not(empty(test.valor))}")});
//
//        UIField proxy = (UIField) create(factory, uiDelegate,
//                new String[]{"gethint", "isrendered"},
//                new Attribute[]{AttributeDef.HINT, AttributeDef.RENDER},
//                new ValueBindingExpression[]{expressionFactory.create("${test.valor}"),
//                        expressionFactory.create("${not(empty(test.valor))}")});

        Assert.assertEquals(expectedHint, proxy.getHint());
        Assert.assertEquals(true, proxy.isRendered(null));
        Assert.assertEquals(expectedPattern, proxy.getPattern());
        Assert.assertEquals(3, proxy.getValueBindingExpressions().size());
    }

    @Test
    public void testCreateProxyBBuddy() throws InstantiationException, IllegalAccessException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String path = appContext.getCacheDir().getPath();

        ClassLoadingStrategy strategy = new AndroidClassLoadingStrategy.Wrapping(new File(path));

        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .method(ElementMatchers.named("toString"))
                .intercept(FixedValue.value("Hello World!"))
                .make()
                .load(getClass().getClassLoader(), strategy)
                .getLoaded();
        assertThat(dynamicType.newInstance().toString(), is("Hello World!"));
    }


    public UIComponent create(UIComponentProxyFactory factory, Object delegate, String[] methodNames, Attribute[] attributes, ValueBindingExpression[] expressions) {
        Class clazz = delegate.getClass();

        String path = System.getProperty("formic.classCache");
        ClassLoadingStrategy strategy = new AndroidClassLoadingStrategy.Wrapping(new File(path));

        try {
            Class<?> dynamicType = new ByteBuddy()
                    .subclass(clazz)
                    .defineField("handler", InvocationHandler.class, Visibility.PUBLIC)
                    .method(ElementMatchers.any())
                    .intercept(InvocationHandlerAdapter.toField("handler"))
                    .make()
                    .load(getClass().getClassLoader(), strategy)
                    .getLoaded();

            Object instance = dynamicType.newInstance();

            UIComponentInvocationHandler handler = new UIComponentInvocationHandler(delegate, methodNames, expressions);
            handler.setFactory(factory);

            Field handlerField = instance.getClass()
                    .getDeclaredField("handler");
            handlerField.setAccessible(true);
            handlerField.set(instance, handler);

            return (UIComponent) instance;
        } catch (Exception e) {
            throw new ConfigurationException("Cannot create dynamic proxy for delegate: " + delegate, e);
        }
    }
}
