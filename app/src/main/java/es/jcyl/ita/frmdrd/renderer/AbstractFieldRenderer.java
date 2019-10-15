package es.jcyl.ita.frmdrd.renderer;

import android.content.Context;
import android.view.ViewGroup;

import javax.inject.Inject;

import es.jcyl.ita.frmdrd.DaggerDiComponent;
import es.jcyl.ita.frmdrd.ui.form.Field;

public abstract class AbstractFieldRenderer implements IFieldRenderer{

    @Inject
    protected OnChangeFieldInterceptor onChangeInterceptor;


    public AbstractFieldRenderer(){
        //onChangeInterceptor = new OnChangeFieldInterceptor();
        DaggerDiComponent.create().inject(this);
    }

    public abstract void render(Context context, Field field,
                                ViewGroup parent);

    public abstract void render(int viewId, Field field);


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
