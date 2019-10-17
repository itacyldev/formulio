package es.jcyl.ita.frmdrd.render;

import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;

import es.jcyl.ita.frmdrd.ui.form.Field;

public class OnChangeFieldInterceptor {


    @Inject
    public OnChangeFieldInterceptor() {

    }

    public void onChange(Field field, Object value) {

        if (validate(field, value)) {
            updateContext(field, value);

            String rerender = field.getUpdate();
            if (StringUtils.isNotEmpty(rerender)) {
                this.rerender(rerender);
            }
        }
    }

    private boolean validate(Field field, Object value) {

        return true;
    }

    private void updateContext(Field field, Object value) {

    }

    private void rerender(String rerender) {

    }

}
