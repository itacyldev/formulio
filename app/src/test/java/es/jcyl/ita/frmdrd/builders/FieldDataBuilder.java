package es.jcyl.ita.frmdrd.builders;

import org.apache.commons.lang.RandomStringUtils;

import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.UIField;


public class FieldDataBuilder extends AbstractDataBuilder<UIField> {
    ValueExpressionFactory exprFactory = new ValueExpressionFactory();

    public FieldDataBuilder() {
        this.baseModel = createEmptyModel();
    }

    @Override
    protected UIField doBuild(UIField templateModel) {
        UIField model = super.doBuild(templateModel);
        // enums aren't properly copied
        model.setType(UIField.TYPE.valueOf(templateModel.getType()));
        return model;
    }

    public FieldDataBuilder withId(String id) {
        this.baseModel.setId(id);
        return this;
    }

    public FieldDataBuilder withFieldType(UIField.TYPE type) {
        this.baseModel.setType(type);
        String id = this.baseModel.getType().toLowerCase() + RandomStringUtils.randomAlphanumeric(5);
        this.baseModel.setId(id);
        return this;
    }

    public FieldDataBuilder withParent(UIComponent parent) {
        this.baseModel.setParent(parent);
        return this;
    }

    public FieldDataBuilder withValueBindingExpression(String expression, Class expectedType) {
        ValueExpressionFactory exprFactory = new ValueExpressionFactory();
        this.baseModel.setValueExpression(exprFactory.create(expression, expectedType));
        return this;
    }
    public FieldDataBuilder withValueBindingExpression(String expression) {
        ValueExpressionFactory exprFactory = new ValueExpressionFactory();
        this.baseModel.setValueExpression(exprFactory.create(expression, String.class));
        return this;
    }
    public FieldDataBuilder withLabel(String label) {
        this.baseModel.setLabel(label);
        return this;
    }

    public FieldDataBuilder withRenderExpression(String renderExpression) {
        this.baseModel.setRenderExpression(exprFactory.create(renderExpression));
        return this;
    }


    @Override
    protected UIField getModelInstance() {
        return new UIField();
    }

    @Override
    public FieldDataBuilder withRandomData() {
        this.baseModel.setLabel(RandomStringUtils.randomAlphanumeric(8));
        this.baseModel.setType(UIField.TYPE.TEXT);
        // set a random value literal expression
        this.baseModel.setValueExpression(exprFactory.create(RandomUtils.randomString(5)));
        String id = this.baseModel.getType().toLowerCase() + RandomStringUtils.randomAlphanumeric(5);
        this.baseModel.setId(id);
        return this;
    }
}
