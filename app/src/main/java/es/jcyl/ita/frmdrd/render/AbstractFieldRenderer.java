package es.jcyl.ita.frmdrd.render;

import android.content.Context;
import android.view.View;

import org.apache.commons.lang.StringUtils;

import es.jcyl.ita.frmdrd.configuration.DataBindings;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.ui.form.UIField;

public abstract class AbstractFieldRenderer implements UIFieldRenderer {

    //@Inject
    protected OnChangeFieldInterceptor onChangeInterceptor;


    public AbstractFieldRenderer(Lifecycle lifecycle) {
        onChangeInterceptor = new OnChangeFieldInterceptor(lifecycle);
        //DaggerDiComponent.create().inject(this);
    }

    public abstract View render(Context context, UIField field);


    @Override
    public void update(UIField field) {
        View view = DataBindings.getView(field.getId());
        String renderCondition = field.getRenderCondition();

        boolean render = true;
        if (StringUtils.isNotEmpty(renderCondition)) {
            render = this.validateCondition(renderCondition);
        }

        view.setVisibility(render ? View.VISIBLE : View.INVISIBLE);
    }

    public void bindField(UIField field, View view) {
        DataBindings.registerView(field.getId(), view);
    }


    protected boolean validateCondition(String renderCondition) {

        return true;
    }
}
