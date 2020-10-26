package es.jcyl.ita.formic.forms.config.builders.ui;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.validation.Validator;
import es.jcyl.ita.formic.forms.validation.ValidatorFactory;

public class ValidatorBuilder extends AbstractComponentBuilder<Validator> {

    public ValidatorBuilder() {
        super("validator", Validator.class);
    }

    @Override
    public Validator build(ConfigNode<Validator> node) {
        String type = node.getAttribute("type");
        Map<String, String> params = getParams(node);

        ValidatorFactory factory = ValidatorFactory.getInstance();
        Validator element = factory.getValidator(type, params);

        setAttributes(element, node);
        node.setElement(element);
        return element;
    }

    @Override
    protected void setAttributes(Validator element, ConfigNode node) {
    }

    private Map<String, String> getParams(ConfigNode<Validator> node) {
        Map<String, String> params = new HashMap<>();
        for (ConfigNode param : node.getChildren()) {
            String name = param.getAttribute("name");
            String value = (String) param.getTexts().get(0);
            params.put(name, value);
        }
        return params;
    }

    @Override
    protected void doWithAttribute(Validator element, String name, String value) {
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode node) {
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode node) {

    }


}