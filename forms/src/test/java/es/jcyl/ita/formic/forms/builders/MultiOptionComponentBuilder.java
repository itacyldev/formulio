package es.jcyl.ita.formic.forms.builders;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.option.UIOption;
import es.jcyl.ita.formic.forms.components.select.UISelect;
import es.jcyl.ita.formic.repo.test.utils.RandomUtils;


public class MultiOptionComponentBuilder<T extends UISelect> extends AbstractDataBuilder<T> {

    private Class componentClass = null;
    private List<UIOption> options;

    public MultiOptionComponentBuilder(Class effectiveClass) {
        super(effectiveClass);
        this.componentClass = effectiveClass;
        this.options = new ArrayList<UIOption>();
        this.baseModel = createEmptyModel();
    }

    @Override
    protected T getModelInstance() {
        if (this.componentClass == null) {
            return null;
        }
        try {
            return (T) this.componentClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to instantiate component class.", e);
        }
    }

    public MultiOptionComponentBuilder withId(String id) {
        this.baseModel.setId(id);
        return this;
    }

    public MultiOptionComponentBuilder withParent(UIComponent parent) {
        this.baseModel.setParent(parent);
        return this;
    }

    public MultiOptionComponentBuilder withValueBindingExpression(String expression, Class expectedType) {
        this.baseModel.setValueExpression(exprFactory.create(expression, expectedType));
        return this;
    }

    public MultiOptionComponentBuilder withValueBindingExpression(String expression) {
        this.baseModel.setValueExpression(exprFactory.create(expression, String.class));
        return this;
    }

    public MultiOptionComponentBuilder withLabel(String label) {
        this.baseModel.setLabel(label);
        return this;
    }

    public MultiOptionComponentBuilder withRenderExpression(String renderExpression) {
        this.baseModel.setRenderExpression(exprFactory.create(renderExpression));
        return this;
    }

    public MultiOptionComponentBuilder addOption(String label, String value) {
        this.options.add(new UIOption(label, value));
        return this;
    }

    public MultiOptionComponentBuilder withOptionValues(String[] labels, String[] values) {
        for (int i = 0; i < values.length; i++) {
            this.options.add(new UIOption(labels[i], values[i]));
        }
        return this;
    }

    public MultiOptionComponentBuilder withNumOptions(int num) {
        for (int i = 0; i < num; i++) {
            String txt = RandomUtils.randomString(5);
            addOption(txt, txt);
        }
        return this;
    }

    @Override
    public MultiOptionComponentBuilder withRandomData() {
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
