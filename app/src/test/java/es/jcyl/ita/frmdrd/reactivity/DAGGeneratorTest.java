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
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;


public class DAGGeneratorTest {

    @Test
    public void createDags() {
        UIForm form = createForm();

        DAGGenerator dagGenerator = new DAGGenerator();

        Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags =
                dagGenerator.createDags(form);


        // Get each field's dag
        DirectedAcyclicGraph dag1 = dags.get("field1");
        DirectedAcyclicGraph dag2 = dags.get("field2");
        DirectedAcyclicGraph dag3 = dags.get("field3");
        DirectedAcyclicGraph dag4 = dags.get("field4");
        DirectedAcyclicGraph dag5 = dags.get("field5");
        DirectedAcyclicGraph dag6 = dags.get("field6");
        DirectedAcyclicGraph dag7 = dags.get("field7");


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
        UIForm form = createFormWithCycle();

        DAGGenerator dagGenerator = new DAGGenerator();

        Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags =
                dagGenerator.createDags(form);
    }

    private UIForm createForm() {
        FieldBuilder fieldBuilder = new FieldBuilder();

        List<UIComponent> fields = new ArrayList<>();
        UIField field1 =
                fieldBuilder.withId("field1").withFieldType(UIField.TYPE.TEXT).withLabel(
                        "field 1").withRerender("field2").build();
        fields.add(field1);

        UIField field3 =
                fieldBuilder.withId("field3").withFieldType(UIField.TYPE.TEXT).withLabel(
                        "field 3").build();
        fields.add(field3);

        UIField field4 =
                fieldBuilder.withId("field4").withFieldType(UIField.TYPE.TEXT).withLabel(
                        "field 4").build();
        fields.add(field4);

        UIField field2 =
                fieldBuilder.withId("field2").withFieldType(UIField.TYPE.TEXT).withLabel(
                        "field 2").withRerender(field3).withRerender(field4).build();
        fields.add(field2);

        UIField field5 =
                fieldBuilder.withId("field5").withFieldType(UIField.TYPE.TEXT).withLabel(
                        "field 5").withRerender("field6").build();
        fields.add(field5);

        UIField field6 =
                fieldBuilder.withId("field6").withFieldType(UIField.TYPE.TEXT).withLabel(
                        "field 6").build();
        fields.add(field6);

        UIField field7 =
                fieldBuilder.withId("field7").withFieldType(UIField.TYPE.TEXT).withLabel(
                        "field 7").build();
        fields.add(field7);

        FormBuilder formBuilder = new FormBuilder();
        formBuilder.withId("form").withChildren(fields).withLabel("Form 1");

        return formBuilder.build();
    }

    private UIForm createFormWithCycle() {
        FieldBuilder fieldBuilder = new FieldBuilder();

        List<UIComponent> fields = new ArrayList<>();
        UIField field1 =
                fieldBuilder.withId("field1").withFieldType(UIField.TYPE.TEXT).withLabel(
                        "field 1").withRerender("field2").build();
        fields.add(field1);

        UIField field2 =
                fieldBuilder.withId("field2").withFieldType(UIField.TYPE.TEXT).withLabel(
                        "field 2").withRerender("field3").build();
        fields.add(field2);

        UIField field3 =
                fieldBuilder.withId("field3").withFieldType(UIField.TYPE.TEXT).withLabel(
                        "field 3").withRerender("field1").build();
        fields.add(field3);

        FormBuilder formBuilder = new FormBuilder();
        formBuilder.withId("form").withChildren(fields).withLabel("Form 1");

        return formBuilder.build();
    }
}
