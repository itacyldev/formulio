package es.jcyl.ita.formic.forms.config.builders.ui;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.UIInputComponent;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.validation.Validator;
import es.jcyl.ita.formic.forms.validation.ValidatorFactory;

public class ValidatorBuilder extends AbstractComponentBuilder<Validator> {

    ValidatorFactory factory = ValidatorFactory.getInstance();

    public ValidatorBuilder() {
        super("validator", Validator.class);
    }

    @Override
    protected Validator instantiate(ConfigNode<Validator> node) {
        String type = node.getAttribute("type");
        Map<String, String> params = getParams(node);
        Validator element = factory.getValidator(type, params);
        return element;
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
        // Do nothing
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<Validator> node) {
        // Do nothing
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<Validator> node) {
        Validator element = node.getElement();
        // attach to parent element
        UIComponent component = (UIComponent) node.getParent().getElement();
        if (!(component instanceof UIInputComponent)) {
            throw new ConfigurationException(DevConsole.error(String.format("The <validator/> tag " +
                    "must be nested in an UIInputComponent, found: [%s].", node.getName())));
        }
        ((UIInputComponent) component).addValidator(element);
    }

}
