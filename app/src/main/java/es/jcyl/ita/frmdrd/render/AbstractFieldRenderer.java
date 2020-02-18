package es.jcyl.ita.frmdrd.render;

import android.content.Context;
import android.view.View;

import org.apache.commons.lang.StringUtils;

import es.jcyl.ita.frmdrd.configuration.DataBindings;
import es.jcyl.ita.frmdrd.lifecycle.Lifecycle;
import es.jcyl.ita.frmdrd.processors.GroovyProcessor;
import es.jcyl.ita.frmdrd.ui.form.UIField;

public abstract class AbstractFieldRenderer implements UIFieldRenderer {

    Context context;
    Lifecycle lifecycle;

    //@Inject
    protected OnChangeFieldInterceptor onChangeInterceptor;


    public AbstractFieldRenderer(Context context, Lifecycle lifecycle) {
        this.context = context;
        this.lifecycle = lifecycle;
        onChangeInterceptor = new OnChangeFieldInterceptor(lifecycle);
        //DaggerDiComponent.create().inject(this);
    }

    public abstract View render(UIField field);


    @Override
    public void update(UIField field) {
        View view = DataBindings.getView(field.getId());
        String renderCondition = field.getRenderCondition();


        boolean render = true;
        if (StringUtils.isNotEmpty(renderCondition)) {
            render = this.validateCondition(renderCondition, field.getId());
        }

        view.setVisibility(render ? View.VISIBLE : View.INVISIBLE);
    }

    public void bindField(UIField field, View view) {
        DataBindings.registerView(field.getId(), view);
    }


    protected boolean validateCondition(String renderCondition,
                                        String fieldId) {
        GroovyProcessor processor =
                new GroovyProcessor(context.getDir(
                        "dynclasses", 0), context.getClassLoader());
        String filename = fieldId+"_renderCond";
        Object result = processor.evaluate(renderCondition, filename,
                lifecycle.getMainContext());

        return Boolean.parseBoolean(result.toString());
    }
}
