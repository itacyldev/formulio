package es.jcyl.ita.frmdrd.render;

import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;

import es.jcyl.ita.frmdrd.ui.form.UIField;

public class OnChangeFieldInterceptor {


    @Inject
    public OnChangeFieldInterceptor() {

    }

    public void onChange(UIField UIField, Object value) {

        if (validate(UIField, value)) {
            updateContext(UIField, value);

            String rerender = UIField.getUpdate();
            if (StringUtils.isNotEmpty(rerender)) {
                this.rerender(rerender);
            }
        }
    }

    private boolean validate(UIField UIField, Object value) {

        return true;
    }

    private void updateContext(UIField UIField, Object value) {

    }

    private void rerender(String rerender) {

    }

}
