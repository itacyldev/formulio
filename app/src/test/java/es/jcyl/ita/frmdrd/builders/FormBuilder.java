package es.jcyl.ita.frmdrd.builders;

import org.apache.commons.jexl3.JxltEngine;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.ValueBindingExpression;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.util.JexlUtils;

public class FormBuilder extends AbstractDataBuilder<UIForm> {

    private String renderCondition;
    private List<UIField> children;
    private int numFields = 5;
    FieldBuilder fieldBuilder = new FieldBuilder();
    private String[] expressions;
    private Class[] expectedTypes;
    private boolean randomValues = true;


    public FormBuilder() {
        this.baseModel = createEmptyModel();
    }

    public FormBuilder withId(String id) {
        this.baseModel.setId(id);
        return this;
    }

    public FormBuilder withLabel(String label) {
        this.baseModel.setLabel(label);
        return this;
    }

    public FormBuilder withNumFields(int num) {
        this.numFields = num;
        return this;
    }

    /**
     * The form builder will set random values per each field
     *
     * @param doRandomValues: if true, each field will have a random value
     * @return
     */
    public FormBuilder withRandomValues(boolean doRandomValues) {
        this.randomValues = doRandomValues;
        return this;
    }

    public FormBuilder withBindingExpressions(String[] expressions) {
        this.expressions = expressions;
        this.numFields = expressions.length;
        return this;
    }

    public FormBuilder withBindingExpressions(String[] expressions, Class[] expectedTypes) {
        if (expressions.length != expectedTypes.length) {
            throw new IllegalArgumentException("Incompatible array lengths, 'expressions' and " +
                    "'expectedTypes' arrays must have the same length");
        }
        this.expressions = expressions;
        this.expectedTypes = expectedTypes;
        this.numFields = expressions.length;
        return this;
    }

    public FormBuilder withChildren(List<UIComponent> children) {
        this.baseModel.setChildren(children);
        return this;
    }

    public FormBuilder withRenderCondition(String renderCondition) {
        return this;
    }


    @Override
    protected UIForm getModelInstance() {
        return new UIForm();
    }

    @Override
    public FormBuilder withRandomData() {
        UIField.TYPE[] values = UIField.TYPE.values();

        List<UIComponent> fields = new ArrayList<UIComponent>();
        // create some random fields
        for (int i = 0; i < this.numFields; i++) {
            UIField.TYPE fType = values[RandomUtils.nextInt(0, values.length - 1)];
            // field type
            UIField field = fieldBuilder.withRandomData().withFieldType(fType).build();
            JxltEngine.Expression jexlExpr = null;
            ValueBindingExpression ve = null;
            if (this.expressions != null) {
                // set binding expression
                jexlExpr = JexlUtils.createExpression(expressions[i]);
                ve = new ValueBindingExpression(jexlExpr);
                if (this.expectedTypes != null) {
                    ve.setExpectedType(this.expectedTypes[i]);
                }
            } else if (this.randomValues) {
                // create literal expression using random value
                jexlExpr = JexlUtils.createExpression(RandomStringUtils.randomAlphanumeric(10).toUpperCase());
                ve = new ValueBindingExpression(jexlExpr);
            }

            field.setValueExpression(ve);
            fields.add(field);
        }

        this.baseModel.setId("form"+RandomStringUtils.randomAlphanumeric(4));
        this.baseModel.setLabel(RandomStringUtils.randomAlphanumeric(8));
        this.baseModel.setChildren(fields);
        return this;
    }
}
