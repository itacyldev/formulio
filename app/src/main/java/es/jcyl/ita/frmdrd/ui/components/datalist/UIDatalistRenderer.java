package es.jcyl.ita.frmdrd.ui.components.datalist;

import android.widget.LinearLayout;
import android.widget.ListView;

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

        LinearLayout tableView = widget.findViewById(R.id.list_layout);

        ListView bodyView = tableView.findViewById(R.id.list_view);

        widget.load(env);
    }
}
