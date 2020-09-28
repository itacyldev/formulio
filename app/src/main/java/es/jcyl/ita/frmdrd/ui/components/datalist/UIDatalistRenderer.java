package es.jcyl.ita.frmdrd.ui.components.datalist;

import android.widget.LinearLayout;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.view.render.AbstractRenderer;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

public class UIDatalistRenderer extends AbstractRenderer<UIDatalist, DatalistWidget> {
    @Override
    protected int getWidgetLayoutId() {
        return 0;
    }

    @Override
    protected void composeWidget(RenderingEnv env, DatalistWidget widget) {

    }

    @Override
    protected DatalistWidget createWidget(RenderingEnv env, UIDatalist component) {
        DatalistWidget widget = super.createWidget(env, component);
        widget.setTag(getWidgetViewTag(component));
        return widget;
    }

    @Override
    protected void setupWidget(RenderingEnv env, DatalistWidget widget) {
        super.setupWidget(env, widget);

        LinearLayout datalistView = widget.findViewById(R.id.datalist_layout);

        LinearLayout datalistContentLayout = datalistView.findViewById(R.id.datalist_content_layout);
        widget.setContentLayout(datalistContentLayout);
        widget.load(env);
    }
}
