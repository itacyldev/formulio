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

    public void bindField(UIField field, View view){
        DataBindings.registerView(field.getId(),view);
    }


    protected boolean validateCondition(String renderCondition) {
        /*JexlBuilder builder = new JexlBuilder();
        JexlEngine jexl = builder.cache(512).strict(true).silent(false).create();
        JexlExpression e = jexl.createExpression(renderCondition);

        JexlContext context = new MapContext();
        context.set("$1", 1);
        context.set("$2", 2);

        Boolean result = (Boolean) e.evaluate(context);*/

        return true;
    }
}
