package es.jcyl.ita.frmdrd.render;
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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.builders.EntityDataBuilder;
import es.jcyl.ita.crtrepo.builders.EntityMetaDataBuilder;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.frmdrd.actions.ActionController;
import es.jcyl.ita.frmdrd.builders.FieldDataBuilder;
import es.jcyl.ita.frmdrd.builders.FormDataBuilder;
import es.jcyl.ita.frmdrd.configuration.ConfigConverters;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.UIField;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;
import es.jcyl.ita.frmdrd.utils.ContextUtils;
import es.jcyl.ita.frmdrd.utils.DevFormBuilder;
import es.jcyl.ita.frmdrd.view.converters.ViewValueConverterFactory;
import es.jcyl.ita.frmdrd.view.dag.DAGManager;
import es.jcyl.ita.frmdrd.view.dag.DAGNode;
import es.jcyl.ita.frmdrd.view.dag.ViewDAG;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;
import es.jcyl.ita.frmdrd.view.render.ViewRenderHelper;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 */
@RunWith(RobolectricTestRunner.class)
public class DelegatedExpressionTest {

    FormDataBuilder formBuilder = new FormDataBuilder();
    EntityDataBuilder entityBuilder;
    EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
    ViewValueConverterFactory convFactory = ViewValueConverterFactory.getInstance();
    ValueExpressionFactory exprFactory = new ValueExpressionFactory();
    ViewRenderHelper renderHelper = new ViewRenderHelper();

    @BeforeClass
    public static void setUp() {
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    /**
     * Creates a view tree and applies rendering conditions based on entity values and checks
     * the view is not visible
     */
    @Test
    public void testSimpleViewWithStringCondsRender() {
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();

        Object[] values = createDepsTree(ctx);
        UIForm form = (UIForm) values[0];
        ViewDAG viewDAG = (ViewDAG) values[1];

        Entity entity = createEntity();

        // set root dependency towards the entity f0 field
        String propertyName = entity.getMetadata().getProperties()[0].getName();
        String entityExpr = String.format("${entity.%s}", propertyName);
        // setup f1 so its value will be obtained from entity
        form.getElement("f1").setValueExpression(exprFactory.create(entityExpr));

        // set entity in forms context
        form.getContext().setEntity(entity);
        RenderingEnv env = new RenderingEnv(ContextUtils.createGlobalContext(), new ActionController());
        env.setViewDAG(viewDAG);
        // walk the tree executing expressions
        View baseFormView = renderHelper.render(ctx, env, form.getParent());

        System.out.println("-------------------");
        // check dependencies chain f1, f2, f3 and f4 must have the same value
        Object expectedValue = entity.get(propertyName);
        for (UIComponent kid : form.getChildren()) {
            UIField field = (UIField) kid;
            Assert.assertEquals(expectedValue.toString(), field.getValue(env.getContext()).toString());
        }
    }

    private Object[] createDepsTree(Context ctx) {
        // create one field form
        DevFormBuilder.CreateOneFieldForm recipe = new DevFormBuilder.CreateOneFieldForm().invoke(ctx);
        UIForm form = recipe.form;
        UIField f1 = recipe.field;
        f1.setId("f1");
//        form.setId("form");
        FieldDataBuilder fBuilder = new FieldDataBuilder();
//        UIField f2 = fBuilder.withRandomData().withId("f2").withValueBindingExpression("${" + form.getId() + ".view.f1}").build();
//        UIField f3 = fBuilder.withRandomData().withId("f3").withValueBindingExpression("${" + form.getId() + ".view.f2}").build();
//        UIField f4 = fBuilder.withRandomData().withId("f4").withValueBindingExpression("${" + form.getId() + ".view.f2}").build();
        UIField f2 = fBuilder.withRandomData().withId("f2").withValueBindingExpression("${view.f1}").build();
        UIField f3 = fBuilder.withRandomData().withId("f3").withValueBindingExpression("${view.f2}").build();
        UIField f4 = fBuilder.withRandomData().withId("f4").withValueBindingExpression("${view.f2}").build();

        // add fields to form
        form.addChild(f2, f3, f4);
        UIView view = new UIView("view1");
        view.addChild(form);


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
