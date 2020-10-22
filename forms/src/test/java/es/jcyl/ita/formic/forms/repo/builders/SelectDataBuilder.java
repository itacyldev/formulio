package es.jcyl.ita.formic.forms.repo.builders;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.repo.test.utils.RandomUtils;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.option.UIOption;
import es.jcyl.ita.formic.forms.components.select.UISelect;


public class SelectDataBuilder extends AbstractDataBuilder<UISelect> {

    private List<UIOption> options;

    public SelectDataBuilder() {
        this.options = new ArrayList<UIOption>();
        this.baseModel = createEmptyModel();
    }

    @Override
    protected UISelect getModelInstance() {
        return new UISelect();
    }

    public SelectDataBuilder withId(String id) {
        this.baseModel.setId(id);
        return this;
    }

    public SelectDataBuilder withParent(UIComponent parent) {
        this.baseModel.setParent(parent);
        return this;
    }

    public SelectDataBuilder withValueBindingExpression(String expression, Class expectedType) {
        this.baseModel.setValueExpression(exprFactory.create(expression, expectedType));
        return this;
    }

    public SelectDataBuilder withValueBindingExpression(String expression) {
        this.baseModel.setValueExpression(exprFactory.create(expression, String.class));
        return this;
    }

    public SelectDataBuilder withLabel(String label) {
        this.baseModel.setLabel(label);
        return this;
    }

    public SelectDataBuilder withRenderExpression(String renderExpression) {
        this.baseModel.setRenderExpression(exprFactory.create(renderExpression));
        return this;
    }

    public SelectDataBuilder addOption(String label, String value) {
        this.options.add(new UIOption(label, value));
        return this;
    }

    public SelectDataBuilder withOptionValues(String[] labels, String[] values) {
        for (int i = 0; i < values.length; i++) {
            this.options.add(new UIOption(labels[i], values[i]));
        }
        return this;
    }

    public SelectDataBuilder withNumOptions(int num) {
        for (int i = 0; i < num; i++) {
            String txt = RandomUtils.randomString(5);
            addOption(txt, txt);
        }
        return this;
    }

    @Override
    public SelectDataBuilder withRandomData() {
        this.baseModel.setLabel(RandomStringUtils.randomAlphanumeric(8));
        // set a random value literal expression
        this.baseModel.setValueExpression(exprFactory.create(RandomUtils.randomString(5)));
        String id = RandomStringUtils.randomAlphanumeric(5);
        this.baseModel.setId(id);
        return this;
    }

    @Override
    protected UISelect doBuild(UISelect templateModel) {
        templateModel.setOptions(this.options.toArray(new UIOption[options.size()]));
        return templateModel;
    }

}
