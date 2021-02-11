package es.jcyl.ita.formic.forms.view.dag;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.builders.FieldDataBuilder;
import es.jcyl.ita.formic.forms.builders.FormDataBuilder;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.inputfield.UIField;
import es.jcyl.ita.formic.forms.components.view.UIView;
import es.jcyl.ita.formic.forms.utils.DevFormBuilder;


public class DAGManagerTest {

    @Test
    public void createDags() {
        UIView view = createView("view");

        DAGManager dagManager = DAGManager.getInstance();
        dagManager.flush();
        dagManager.generateDags(view);

        ViewDAG viewDags = dagManager.getViewDAG(view.getId());

        Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags = viewDags.getDags();

        Assert.assertEquals(2, dags.size());

        // Get each field's dag
        Graph<DAGNode, DefaultEdge> dag1 = viewDags.getDAG("form.field1");
        Graph<DAGNode, DefaultEdge> dag2 = viewDags.getDAG("form.field5");

        Assert.assertEquals(4, dag1.vertexSet().size());
        Assert.assertEquals(2, dag2.vertexSet().size());

    }

    @Test(expected = IllegalArgumentException.class)
    public void createDagWithCycle() {
        UIView view = createViewWithCycle();

        DAGManager dagManager = DAGManager.getInstance();
        dagManager.generateDags(view);
    }

    @Test
    public void createDAG2Views() {
        UIView view1 = createView("view1");
        UIView view2 = createView("view2");

        DAGManager dagManager = DAGManager.getInstance();
        dagManager.flush();

        dagManager.generateDags(view1);
        dagManager.generateDags(view2);

        ViewDAG viewDag1 = dagManager.getViewDAG(view1.getId());
        ViewDAG viewDag2 = dagManager.getViewDAG(view2.getId());

        Assert.assertNotNull(viewDag1);
        Assert.assertNotNull(viewDag2);

        Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags1 = viewDag1.getDags();
        Map<String, DirectedAcyclicGraph<DAGNode, DefaultEdge>> dags2 = viewDag2.getDags();

        Assert.assertNotNull(dags1);
        Assert.assertNotNull(dags2);
    }

    private UIView createView(String viewId) {
        UIView view = new UIView(viewId);
        UIForm form = createForm("form");
        form.setRoot(view);
        view.addChild(form);

        return view;
    }

    private UIView createViewWithCycle() {
        UIView view = new UIView("view2");
        UIForm form = createFormWithCycle("form");
        form.setRoot(view);
        view.addChild(form);

        return view;
    }

    private UIForm createForm(String formId) {

        UIForm form = DevFormBuilder.createOneFieldForm();
        form.setId(formId);

        FieldDataBuilder fieldBuilder = new FieldDataBuilder();

        UIInputComponent field1 = form.getFields().get(0);
        field1.setId("field1");
        UIField field2 =
                fieldBuilder.withFieldType(UIField.TYPE.TEXT).withId("field2").withLabel(
                        "field 2").withValueBindingExpression("${" + field1.getAbsoluteId() + "}", String.class).build();
        form.addChild(field2);

        UIField field3 =
                fieldBuilder.withFieldType(UIField.TYPE.TEXT).withId("field3").withLabel(
                        "field 3").withValueBindingExpression("${" + field2.getAbsoluteId() + "}", String.class).build();
        form.addChild(field3);

        UIField field4 =
                fieldBuilder.withFieldType(UIField.TYPE.TEXT).withId("field4").withLabel(
                        "field 4").withValueBindingExpression("${" + field2.getAbsoluteId() + "}", String.class).build();
        form.addChild(field4);


        UIField field5 =
                fieldBuilder.withFieldType(UIField.TYPE.TEXT).withId("field5").withLabel(
                        "field 5").build();
        form.addChild(field5);

        UIField field6 =
                fieldBuilder.withFieldType(UIField.TYPE.TEXT).withId("field6").withLabel(
                        "field 6").withValueBindingExpression("${" + field5.getAbsoluteId() + "}", String.class).build();
        form.addChild(field6);

        UIField field7 =
                fieldBuilder.withFieldType(UIField.TYPE.TEXT).withId("field7").withLabel(
                        "field 7").build();
        form.addChild(field7);

        return form;
    }

    private UIForm createFormWithCycle(String formId) {
        FieldDataBuilder fieldBuilder = new FieldDataBuilder();

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

        FormDataBuilder formBuilder = new FormDataBuilder();
        formBuilder.withId(formId).withChildren(fields);

        return formBuilder.build();
    }
}

