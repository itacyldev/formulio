package es.jcyl.ita.frmdrd.builders;

import org.apache.commons.lang.RandomStringUtils;

import java.util.List;

import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;
import es.jcyl.ita.frmdrd.ui.components.inputfield.UIField;

public class FormBuilder extends AbstractDataBuilder<UIForm> {



    private String renderCondition;
    private List<UIField> children;

    public FormBuilder() {
        this.baseModel = createEmptyModel();
    }

    public FormBuilder withId(String id){
        this.baseModel.setId(id);
        return this;
    }

    public FormBuilder withLabel(String label) {
        this.baseModel.setLabel(label);
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
        this.baseModel.setId(RandomStringUtils.randomAlphanumeric(8));
        this.baseModel.setLabel(RandomStringUtils.randomAlphanumeric(8));
        return this;
    }
}
