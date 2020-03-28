package es.jcyl.ita.frmdrd.builders;

import org.apache.commons.jexl3.JxltEngine;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.ValueBindingExpression;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;
import es.jcyl.ita.frmdrd.util.JexlUtils;


public class FieldBuilder extends AbstractDataBuilder<UIField> {

    public FieldBuilder() {
        this.baseModel = createEmptyModel();
    }


    @Override
    protected UIField doBuild(UIField templateModel) {
        UIField model = super.doBuild(templateModel);
        // enums aren't properly copied
        model.setType(UIField.TYPE.valueOf(templateModel.getType()));
        return model;
    }

    public FieldBuilder withId(String id) {
        this.baseModel.setId(id);
        return this;
    }

    public FieldBuilder withFieldType(UIField.TYPE type) {
        this.baseModel.setType(type);
        String id = this.baseModel.getType().toLowerCase() + RandomStringUtils.randomAlphanumeric(5);
        this.baseModel.setId(id);
        return this;
    }

    public FieldBuilder withParent(UIComponent parent) {
        this.baseModel.setParent(parent);
        return this;
    }

    public FieldBuilder withRenderer(String rerenderStr) {
        this.baseModel.setReRender(rerenderStr);
        return this;
    }

    public FieldBuilder withValueBindingExpression(String expression, Class expectedType) {
        JxltEngine.Expression jexlExpr = JexlUtils.createExpression(expression);
        ValueBindingExpression ve  = new ValueBindingExpression(jexlExpr);
        ve.setExpectedType(expectedType);
        this.baseModel.setValueExpression(ve);
        return this;
    }

    public FieldBuilder withRenderer(UIComponent component) {
        String rerenderStr = this.baseModel.getReRender();
        if (StringUtils.isNotEmpty(rerenderStr)) {
            rerenderStr += "," + component.getId();
        } else {
            rerenderStr = component.getId();
        }

        this.baseModel.setReRender(rerenderStr);
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
        this.baseModel.setLabel(RandomStringUtils.randomAlphanumeric(8));
        this.baseModel.setType(UIField.TYPE.TEXT);
        String id = this.baseModel.getType().toLowerCase() + RandomStringUtils.randomAlphanumeric(5);
        this.baseModel.setId(id);
        return this;
    }
}
