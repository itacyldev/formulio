package es.jcyl.ita.frmdrd.builders;

import org.apache.commons.jexl3.JxltEngine;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.frmdrd.EntityToComponentMapper;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.UIField;
import es.jcyl.ita.frmdrd.el.JexlUtils;

public class FormBuilder extends AbstractDataBuilder<UIForm> {

    private String renderCondition;
    private List<UIField> children;
    private int numFields = 5;
    FieldBuilder fieldBuilder = new FieldBuilder();
    private String[] expressions;
    private Class[] expectedTypes;
    private boolean randomValues = true;
    private ValueExpressionFactory exprFactory = new ValueExpressionFactory();

    private EntityToComponentMapper componentMapper = new EntityToComponentMapper();
    private EntityMeta meta;


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

    public FormBuilder withMeta(EntityMeta meta) {
        this.numFields = meta.getProperties().length;
        this.meta = meta;
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
            UIField.TYPE fType;
            if (this.meta == null) {
                // get input type as randomly
                fType = values[RandomUtils.nextInt(0, values.length - 1)];
            } else {
                // get type from given meta
                PropertyType property = this.meta.getProperties()[i];
                fType = this.componentMapper.getComponent(property.type);
            }
            // field type
            fieldBuilder.withRandomData().withFieldType(fType);
            if (meta != null) {
                PropertyType property = this.meta.getProperties()[i];
                fieldBuilder.withId(property.getName());
            }
            UIField field = fieldBuilder.build();
            JxltEngine.Expression jexlExpr;
            ValueBindingExpression ve = null;

            if (this.expressions != null) {
                // set binding expression
                ve = exprFactory.create(expressions[i]);
                if (this.expectedTypes != null) {
                    ve.setExpectedType(this.expectedTypes[i]);
                }
            } else if (this.meta != null) {
                // create expression to refer to entity values
                PropertyType property = this.meta.getProperties()[i];
                ve = exprFactory.create("${entity." + property.name + "}", property.getType());
            } else if (this.randomValues) {
                // create literal expression using random value
                ve = exprFactory.create(RandomStringUtils.randomAlphanumeric(10).toUpperCase());
            }

            field.setValueExpression(ve);
            fields.add(field);
        }

        this.baseModel.setId("form" + RandomStringUtils.randomAlphanumeric(4));
        this.baseModel.setLabel(RandomStringUtils.randomAlphanumeric(8));
        this.baseModel.setChildren(fields);
        return this;
    }
}
