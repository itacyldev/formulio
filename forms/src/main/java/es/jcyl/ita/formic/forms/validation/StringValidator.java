package es.jcyl.ita.formic.forms.validation;

import org.apache.commons.validator.routines.RegexValidator;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.components.UIComponent;

public class StringValidator extends RegexValidator implements Validator {
    public StringValidator(String regex, boolean caseSensitive) {
        super(regex, caseSensitive);
    }

    @Override
    public void validate(Context ctx, UIComponent component, String value) {
        Boolean valid = isValid(value);

        if(!valid){
            throw new ValidatorException("");
        }
    }
}
