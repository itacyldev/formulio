package es.jcyl.ita.frmdrd.render;

import android.content.Context;
import android.view.ViewGroup;

import javax.inject.Inject;

import es.jcyl.ita.frmdrd.DaggerDiComponent;
import es.jcyl.ita.frmdrd.ui.form.UIComponent;

public abstract class AbstractFieldRenderer implements UIComponentRenderer {

    @Inject
    protected OnChangeFieldInterceptor onChangeInterceptor;


    public AbstractFieldRenderer(){
        //onChangeInterceptor = new OnChangeFieldInterceptor();
        DaggerDiComponent.create().inject(this);
    }

    public abstract void render(Context context, UIComponent component,
                                ViewGroup parent);

    public abstract void render(int viewId, UIComponent component);


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
