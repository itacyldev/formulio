package es.jcyl.ita.frmdrd.builders;

import org.apache.commons.lang.RandomStringUtils;

import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.ValueBindingExpression;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;


public class FieldBuilder extends AbstractDataBuilder<UIField> {

    public FieldBuilder() {
        this.baseModel = createEmptyModel();
    }

    public FieldBuilder withId(String id) {
        this.baseModel.setId(id);
        return this;
    }

    public FieldBuilder withFieldType(UIField.TYPE type) {
        this.baseModel.setType(type);
        return this;
    }

    public FieldBuilder withParent(UIComponent parent) {
        this.baseModel.setParent(parent);
        return this;
    }

    public FieldBuilder withValueExpression(ValueBindingExpression valueExpression) {

        this.baseModel.setValueExpression(valueExpression);
        return this;
    }

    public FieldBuilder withLabel(String label) {
        this.baseModel.setLabel(label);
        return this;
    }

    public FieldBuilder withRenderCondition(String renderCondition) {
        this.baseModel.setRenderCondition(renderCondition);
        return this;
    }

    public UIField create() {
        UIField field = new UIField();
        return field;
    }

    @Override
    protected UIField getModelInstance() {
        return new UIField();
    }

    @Override
    public FieldBuilder withRandomData() {
        this.baseModel.setId(RandomStringUtils.randomAlphanumeric(8));
        this.baseModel.setLabel(RandomStringUtils.randomAlphanumeric(8));
        this.baseModel.setType(UIField.TYPE.TEXT);
        return this;
    }
}
