package es.jcyl.ita.frmdrd.builders;

import org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.autocomplete.UIAutoComplete;
import es.jcyl.ita.frmdrd.ui.components.select.UIOption;
import es.jcyl.ita.frmdrd.ui.components.select.UISelect;


public class AutoCompleteDataBuilder extends AbstractDataBuilder<UIAutoComplete> {

    private List<UIOption> options;

    public AutoCompleteDataBuilder() {
        this.options = new ArrayList<UIOption>();
        this.baseModel = createEmptyModel();
    }

    @Override
    protected UIAutoComplete getModelInstance() {
        return new UIAutoComplete();
    }

    public AutoCompleteDataBuilder withId(String id) {
        this.baseModel.setId(id);
        return this;
    }

    public AutoCompleteDataBuilder withParent(UIComponent parent) {
        this.baseModel.setParent(parent);
        return this;
    }

    public AutoCompleteDataBuilder withValueBindingExpression(String expression, Class expectedType) {
        this.baseModel.setValueExpression(exprFactory.create(expression, expectedType));
        return this;
    }

    public AutoCompleteDataBuilder withValueBindingExpression(String expression) {
        this.baseModel.setValueExpression(exprFactory.create(expression, String.class));
        return this;
    }

    public AutoCompleteDataBuilder withLabel(String label) {
        this.baseModel.setLabel(label);
        return this;
    }

    public AutoCompleteDataBuilder withRenderExpression(String renderExpression) {
        this.baseModel.setRenderExpression(exprFactory.create(renderExpression));
        return this;
    }

    public AutoCompleteDataBuilder addOption(String label, String value) {
        this.options.add(new UIOption(label, value));
        return this;
    }

    public AutoCompleteDataBuilder withOptionValues(String[] labels, String[] values) {
        for (int i = 0; i < values.length; i++) {
            this.options.add(new UIOption(labels[i], values[i]));
        }
        return this;
    }

    public AutoCompleteDataBuilder withNumOptions(int num) {
        for (int i = 0; i < num; i++) {
            String txt = RandomUtils.randomString(5);
            addOption(txt, txt);
        }
        return this;
    }

    @Override
    public AutoCompleteDataBuilder withRandomData() {
        this.baseModel.setLabel(RandomStringUtils.randomAlphanumeric(8));
        // set a random value literal expression
        this.baseModel.setValueExpression(exprFactory.create(RandomUtils.randomString(5)));
        String id = RandomStringUtils.randomAlphanumeric(5);
        this.baseModel.setId(id);
        return this;
    }

    @Override
    protected UIAutoComplete doBuild(UIAutoComplete templateModel) {
        templateModel.setOptions(this.options.toArray(new UIOption[options.size()]));
        return templateModel;
    }

}
