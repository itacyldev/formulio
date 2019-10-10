package es.jcyl.ita.frmdrd.renderer;

import android.content.Context;
import android.view.ViewGroup;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

import es.jcyl.ita.frmdrd.ui.form.Field;

public abstract class AbstractFieldRenderer implements IFieldRenderer{


    public abstract void render(Context context, Field field,
                                ViewGroup parent);


    protected boolean validateCondition(String renderCondition) {
        JexlBuilder builder = new JexlBuilder();
        JexlEngine jexl = builder.cache(512).strict(true).silent(false).create();
        JexlExpression e = jexl.createExpression(renderCondition);

        JexlContext context = new MapContext();
        context.set("$1", 1);
        context.set("$2", 2);

        Boolean result = (Boolean) e.evaluate(context);

        return result;
    }
}
