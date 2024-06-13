package es.jcyl.ita.formic.forms.project.handlers;
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

import android.net.Uri;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;

import java.io.File;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.config.reader.xml.XmlConfigFileReader;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.forms.project.ProjectResource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
public class ContextConfigHandlerTest {

    @BeforeClass
    public static void setUp() {
        App.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
    }

    /**
     * Test the context read from configuration is registered in global context
     */
    @Test
    public void testContextRegistering() {
        // preload a context in the global context
        App.getInstance().getGlobalContext().addContext(new BasicContext("ctx0"));

        // manually create the configuration Node that should be return for the contextBuilder
        ConfigNode node = new ConfigNode<CompositeContext>("context");
        CompositeContext ctx = new UnPrefixedCompositeContext();
        ctx.addContext(new BasicContext("ctx1"));
        ctx.addContext(new BasicContext("ctx2"));
        // this context must replace the ctx0 preload in global context
        Context ctx0 = new BasicContext("ctx0");
        ctx0.put("key", "mykey");
        ctx.addContext(ctx0);
        node.setElement(ctx);

        // mock the handler reader
        XmlConfigFileReader readerMock = mock(XmlConfigFileReader.class);
        when(readerMock.read((Uri) any())).thenReturn(node);

        ContextConfigHandler handler = new ContextConfigHandler();
        ReflectionHelpers.setField(handler, "reader", readerMock);

        // call handler with no-parameter, the reader will receive the ConfigNode
        ProjectResource emptyResource = new ProjectResource(null, new File("."),
                ProjectResource.ResourceType.CONTEXT);
        handler.handle(emptyResource);

        // check the context to find ctx1 and ctx2
        Assert.assertTrue(App.getInstance().getGlobalContext().hasContext("ctx1"));
        Assert.assertTrue(App.getInstance().getGlobalContext().hasContext("ctx2"));
        // check the ctx0 context has been replaced
        Assert.assertTrue(App.getInstance().getGlobalContext().hasContext("ctx0"));
        Assert.assertTrue(App.getInstance().getGlobalContext().containsKey("ctx0.key"));

    }
}
