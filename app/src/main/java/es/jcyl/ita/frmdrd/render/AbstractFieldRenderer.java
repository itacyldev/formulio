package es.jcyl.ita.frmdrd.render;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

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

    public abstract void render(Context context, UIField field,
                                ViewGroup parent);

    public abstract void render(int viewId, UIField field);

    public void bindField(UIField field, View view) {
        DataBindings.registerView(field.getId(), view);
    }


    protected boolean validateCondition(String renderCondition) {

        return true;
    }
}
