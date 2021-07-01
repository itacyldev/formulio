package es.jcyl.ita.formic.forms;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.components.inputfield.UIField;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.builders.proxy.UIComponentProxyFactory;
import es.jcyl.ita.formic.forms.config.meta.Attribute;
import es.jcyl.ita.formic.forms.config.meta.AttributeDef;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;

@RunWith(AndroidJUnit4.class)
public class ProxyInstrumentationTest {
    ValueExpressionFactory expressionFactory = ValueExpressionFactory.getInstance();


    @BeforeClass
    public static void init() {
        System.setProperty("formic.classLoading", "android");
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    @Test
    public void testComponent() throws InstantiationException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String path = appContext.getCacheDir().getPath();
        System.setProperty("formic.classCache", path);

        String expectedHint = RandomStringUtils.randomAlphanumeric(10);
        BasicContext ctx = new BasicContext("test");
        ctx.put("valor", expectedHint);

        UIField uiDelegate = new UIField();
        uiDelegate.setValueExpression(expressionFactory.create("${entity.property}"));
        String expectedPatternn = "pattern-abcde";
        uiDelegate.setPattern(expectedPatternn);

        RenderingEnv renderingEnv = MainController.getInstance().getRenderingEnv();
        WidgetContext widgetContext = new WidgetContext();
        widgetContext.addContext(ctx);
        renderingEnv.setWidgetContext(widgetContext);


        UIComponentProxyFactory factory = UIComponentProxyFactory.getInstance();

        factory.setEnv(renderingEnv);

        UIField proxy = (UIField) factory.create(uiDelegate,
                new String[]{"gethint", "isrendered"},
                new Attribute[]{AttributeDef.HINT, AttributeDef.RENDER},
                new ValueBindingExpression[]{expressionFactory.create("${test.valor}"),
                        expressionFactory.create("${not(empty(test.valor))}")});

        Assert.assertEquals(expectedHint, proxy.getHint(null));
        Assert.assertEquals(true, proxy.isRendered(null));
        Assert.assertEquals(expectedPatternn, proxy.getPattern());
        Assert.assertEquals(3, proxy.getValueBindingExpressions().size());
    }
}
