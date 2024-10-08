package es.jcyl.ita.formic.forms.view.render;
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

import android.content.Context;
import android.view.View;

import androidx.test.platform.app.InstrumentationRegistry;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.actions.ActionController;
import es.jcyl.ita.formic.forms.builders.FieldDataBuilder;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.inputfield.UIField;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.forms.utils.ContextTestUtils;
import es.jcyl.ita.formic.forms.utils.DevFormBuilder;
import es.jcyl.ita.formic.forms.view.dag.DAGManager;
import es.jcyl.ita.formic.forms.view.dag.DAGNode;
import es.jcyl.ita.formic.forms.view.dag.ViewDAG;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnvFactory;
import es.jcyl.ita.formic.forms.view.render.renderer.ViewRenderer;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.builders.EntityDataBuilder;
import es.jcyl.ita.formic.repo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.formic.repo.meta.EntityMeta;

import static org.mockito.Mockito.*;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class DelegatedExpressionTest {

    EntityDataBuilder entityBuilder;
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();
    ViewRenderer renderHelper = new ViewRenderer();

    Context ctx;

    @Before
    public void setUp() {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        ctx.setTheme(R.style.FormudruidLight);

        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Creates a 4 field dependency tree and check all the fields have the same value at
     * the end of the test
     */
    @Test
    public void testBasicDependencyChain() {
        Object[] values = createDepsTree(ctx);
        UIForm form = (UIForm) values[0];
        ViewDAG viewDAG = (ViewDAG) values[1];

        Entity entity = createEntity();

        // set root dependency pointing at entity field f[0]
        String propertyName = entity.getMetadata().getProperties()[0].getName();
        String entityExpr = String.format("${entity.%s}", propertyName);
        // setup f1 so its value will be obtained from entity
        form.getChildById("f1").setValueExpression(exprFactory.create(entityExpr));

        // set entity in forms context
        form.setEntity(entity);
        ActionController mcAC = mock(ActionController.class);
        RenderingEnvFactory.getInstance().setActionController(mcAC);
        RenderingEnv env = RenderingEnvFactory.getInstance().create();

        env.setGlobalContext(ContextTestUtils.createGlobalContext());
        env.setAndroidContext(ctx);
        env.setViewDAG(viewDAG);

        // walk the tree executing expressions
        View baseFormView = renderHelper.render(env, form);

        // check dependencies chain f1, f2, f3 and f4 must have the same value
        Object expectedValue = entity.get(propertyName);
        for (UIInputComponent kid : form.getFields()) {
            UIInputComponent field = kid;
            Assert.assertEquals(expectedValue.toString(), field.getValue(env.getWidgetContext()).toString());
        }
    }

    private Object[] createDepsTree(Context ctx) {
        // create one field form
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm().invoke(ctx);
        UIForm form = recipe.form;
        UIInputComponent f1 = recipe.field;
        f1.setId("f1");
        FieldDataBuilder fBuilder = new FieldDataBuilder();
        UIField f2 = fBuilder.withRandomData().withId("f2").withValueBindingExpression("${view.f1}").build();
        UIField f3 = fBuilder.withRandomData().withId("f3").withValueBindingExpression("${view.f2}").build();
        UIField f4 = fBuilder.withRandomData().withId("f4").withValueBindingExpression("${view.f2}").build();

        // add fields to form
        form.addChild(f2, f3, f4);
        UIView view = new UIView();
        view.setId("view1");
        view.addChild(form);
        form.setRoot(view);

        // create dependency tree
        DAGManager dagManager = DAGManager.getInstance();
        dagManager.generateDags(view);

        ViewDAG viewDAG = dagManager.getViewDAG(view.getId());

        Collection<DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags = viewDAG.getDags().values();
        List<DirectedAcyclicGraph<DAGNode, DefaultEdge>> lstDags = new ArrayList<>();
        lstDags.addAll(dags);
        // The dags are repeated, keep just the first one until we fix this problem

        return new Object[]{form, viewDAG};
    }

    private Entity createEntity() {
        EntityMeta meta = metaBuilder.withNumProps(1).build();
        entityBuilder = new EntityDataBuilder(meta);
        Entity entity = entityBuilder.withRandomData().build();
        return entity;
    }
}
