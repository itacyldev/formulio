package es.jcyl.ita.frmdrd.config.builders;

import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.validation.Validator;

public class ValidatorBuilder extends AbstractComponentBuilder<Validator> {

    public ValidatorBuilder(String tagName, Class<? extends Validator> clazz) {
        super(tagName, clazz);
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
