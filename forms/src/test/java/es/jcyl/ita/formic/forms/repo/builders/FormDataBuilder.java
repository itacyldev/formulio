package es.jcyl.ita.formic.forms.repo.builders;

import org.apache.commons.jexl3.JxltEngine;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.mini2Dx.beanutils.BeanUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.components.inputfield.UIField;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;

public class FormDataBuilder extends AbstractDataBuilder<UIForm> {

    private String renderCondition;
    private List<UIField> children;
    private int numFields = 5;
    FieldDataBuilder fieldBuilder = new FieldDataBuilder();
    private String[] expressions;
    private Class[] expectedTypes;
    private boolean randomValues = true;
    private ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();

    private EntityToComponentMapper componentMapper = new EntityToComponentMapper();
    private EntityMeta meta;


    public FormDataBuilder() {
        this.baseModel = createEmptyModel();
    }

    public FormDataBuilder withId(String id) {
        this.baseModel.setId(id);
        return this;
    }

    public FormDataBuilder withLabel(String label) {
        this.baseModel.setLabel(label);
        return this;
    }

    public FormDataBuilder withNumFields(int num) {
        this.numFields = num;
        return this;
    }

    /**
     * The form builder will set random values per each field
     *
     * @param doRandomValues: if true, each field will have a random value
     * @return
     */
    public FormDataBuilder withRandomValues(boolean doRandomValues) {
        this.randomValues = doRandomValues;
        return this;
    }

    public FormDataBuilder withBindingExpressions(String[] expressions) {
        this.expressions = expressions;
        this.numFields = expressions.length;
        return this;
    }

    public FormDataBuilder withBindingExpressions(String[] expressions, Class[] expectedTypes) {
        if (expressions.length != expectedTypes.length) {
            throw new IllegalArgumentException("Incompatible array lengths, 'expressions' and " +
                    "'expectedTypes' arrays must have the same length");
        }
        this.expressions = expressions;
        this.expectedTypes = expectedTypes;
        this.numFields = expressions.length;
        return this;
    }

    public FormDataBuilder withMeta(EntityMeta meta) {
        this.numFields = meta.getProperties().length;
        this.meta = meta;
        return this;

    }

    public FormDataBuilder withChildren(List<UIComponent> children) {
        this.baseModel.setChildren(children);
        return this;
    }

    public FormDataBuilder withRenderCondition(String renderCondition) {
        return this;
    }


    @Override
    protected UIForm getModelInstance() {
        return new UIForm();
    }

    @Override
    public FormDataBuilder withRandomData() {
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
        this.baseModel.setReadOnly(false);
        return this;
    }

    protected UIForm doBuild(UIForm templateModel) {
        UIForm model = createEmptyModel();
        try {
            BeanUtils.copyProperties(model, templateModel);
        } catch (Exception e) {
            throw new DataBuilderException("An error occurred while trying to copy data from the model: "
                    + model.toString(), e);
        }
        model.setReadOnly(false);
        return model;
    }
}
