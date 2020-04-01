package es.jcyl.ita.frmdrd.reactivity;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.frmdrd.builders.FieldBuilder;
import es.jcyl.ita.frmdrd.builders.FormBuilder;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.ui.components.view.UIView;


public class DAGManagerTest {

    @Test
    public void createDags() {
        UIView view = createView();

        DAGManager.getInstance().getDags().clear();
        DAGManager dagManager = new DAGManager();
        dagManager.generateDags(view);

        Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags = DAGManager.getInstance().getDags();

        // Get each field's dag
        DirectedAcyclicGraph dag1 = dags.get("form.field1");
        DirectedAcyclicGraph dag2 = dags.get("form.field2");
        DirectedAcyclicGraph dag3 = dags.get("form.field3");
        DirectedAcyclicGraph dag4 = dags.get("form.field4");
        DirectedAcyclicGraph dag5 = dags.get("form.field5");
        DirectedAcyclicGraph dag6 = dags.get("form.field6");
        DirectedAcyclicGraph dag7 = dags.get("form.field7");


        Assert.assertEquals(6, dags.size());

        // Fields 1,2,3 and 4 are nodes in the same DAG
        Assert.assertEquals(dag1, dag2);
        Assert.assertEquals(dag1, dag3);
        Assert.assertEquals(dag1, dag4);

        // Fields 5 and 6 are nodes in the same DAG
        Assert.assertEquals(dag5, dag6);

        // Fields 1 and 5 are nodes in different DAGs
        Assert.assertNotEquals(dag1, dag5);

        // Field 7 has no dependencies therefore it has no DAG
        Assert.assertNull(dag7);


        Assert.assertEquals(4, dag1.vertexSet().size());
        Assert.assertEquals(2, dag5.vertexSet().size());

    }


    @Test(expected = IllegalArgumentException.class)
    public void createDagWithCycle() {
        UIView view = createViewWithCycle();

        DAGManager.getInstance().getDags().clear();
        DAGManager dagManager = new DAGManager();
        dagManager.generateDags(view);
    }

    private UIView createView() {
        UIView view = new UIView("view");
        UIForm form = createForm("form");
        view.addChild(form);

        return view;
    }

    private UIView createViewWithCycle() {
        UIView view = new UIView("view");
        UIForm form = createFormWithCycle("form");
        view.addChild(form);

        return view;
    }

    private UIForm createForm(String formId) {
        FieldBuilder fieldBuilder = new FieldBuilder();

        List<UIComponent> fields = new ArrayList<>();
        UIField field1 =
                fieldBuilder.withFieldType(UIField.TYPE.TEXT).withId("field1").withLabel(
                        "field 1").build();
        fields.add(field1);

        ValueExpressionFactory exprFactory = new ValueExpressionFactory();
        exprFactory.create("${entity.it_profile}", Long.class);
        UIField field2 =
                fieldBuilder.withFieldType(UIField.TYPE.TEXT).withId("field2").withLabel(
                        "field 2").withValueBindingExpression("${form.field1}", String.class).build();

        UIField field3 =
                fieldBuilder.withFieldType(UIField.TYPE.TEXT).withId("field3").withLabel(
                        "field 3").withValueBindingExpression("${form.field2}", String.class).build();
        fields.add(field3);

        UIField field4 =
                fieldBuilder.withFieldType(UIField.TYPE.TEXT).withId("field4").withLabel(
                        "field 4").withValueBindingExpression("${form.field2}", String.class).build();
        fields.add(field4);


        fields.add(field2);

        UIField field5 =
                fieldBuilder.withFieldType(UIField.TYPE.TEXT).withId("field5").withLabel(
                        "field 5").build();
        fields.add(field5);

        UIField field6 =
                fieldBuilder.withFieldType(UIField.TYPE.TEXT).withId("field6").withLabel(
                        "field 6").withValueBindingExpression("${form.field5}", String.class).build();
        fields.add(field6);

        UIField field7 =
                fieldBuilder.withFieldType(UIField.TYPE.TEXT).withId("field7").withLabel(
                        "field 7").build();
        fields.add(field7);

        FormBuilder formBuilder = new FormBuilder();
        formBuilder.withId(formId).withChildren(fields);

        return formBuilder.build();
    }

    private UIForm createFormWithCycle(String formId) {
        FieldBuilder fieldBuilder = new FieldBuilder();

        List<UIComponent> fields = new ArrayList<>();
        UIField field1 =
                fieldBuilder.withFieldType(UIField.TYPE.TEXT).withId("field1").withLabel(
                        "field 1").withValueBindingExpression("${form.field3}", String.class).build();
        fields.add(field1);

        UIField field2 =
                fieldBuilder.withFieldType(UIField.TYPE.TEXT).withId("field2").withLabel(
                        "field 2").withValueBindingExpression("${form.field1}", String.class).build();
        fields.add(field2);

        UIField field3 =
                fieldBuilder.withFieldType(UIField.TYPE.TEXT).withId("field3").withLabel(
                        "field 3").withValueBindingExpression("${form.field2}", String.class).build();
        fields.add(field3);

        FormBuilder formBuilder = new FormBuilder();
        formBuilder.withId(formId).withChildren(fields);

        return formBuilder.build();
    }
}
