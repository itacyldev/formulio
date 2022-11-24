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
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.mini2Dx.collections.CollectionUtils;

import edu.emory.mathcs.backport.java.util.Collections;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.components.view.ViewWidget;
import es.jcyl.ita.formic.forms.context.impl.ViewContext;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.utils.dummy.DummyWidgetContextHolder;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class ScriptViewHelperTest {

    @Test
    public void testFindViewContext() {
        // create widget context holder with its ViewContex
        WidgetContextHolder wCtx = new DummyWidgetContextHolder("myDummyWidget");
        ViewContext expectedViewContext = wCtx.getWidgetContext().getViewContext();

        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(mcAC);
        // set the root view Widget
        ViewWidget viewWidget = mock(ViewWidget.class);
        when(viewWidget.getContextHoldersMap()).thenReturn(Collections.singletonMap("myDummyWidget",wCtx));
        when(viewWidget.getContextHolders()).thenReturn(Collections.singletonList(wCtx));
        env.setRootWidget(viewWidget);

        ScriptViewHelper helper = new ScriptViewHelper(env, ContextTestUtils.createGlobalContext());
        ViewContext viewContext = helper.viewContext("myDummyWidget");

        Assert.assertEquals(expectedViewContext, viewContext);
    }

    @Test
    public void testFindViewContextCollection() {
        // create widget context holder with its ViewContex
        WidgetContextHolder wCtx = new DummyWidgetContextHolder("myDummyWidget");

        ActionController mcAC = mock(ActionController.class);
        RenderingEnv env = new RenderingEnv(mcAC);
        // set the root view Widget
        ViewWidget viewWidget = mock(ViewWidget.class);
        when(viewWidget.getContextHoldersMap()).thenReturn(Collections.singletonMap("myDummyWidget",wCtx));
        when(viewWidget.getContextHolders()).thenReturn(Collections.singletonList(wCtx));
        env.setRootWidget(viewWidget);

        ScriptViewHelper helper = new ScriptViewHelper(env, ContextTestUtils.createGlobalContext());
        ScriptableList<ViewContext> collection = helper.viewContexts();

        Assert.assertTrue(CollectionUtils.isNotEmpty(collection));
    }
}
