package es.jcyl.ita.frmdrd.ui.components.tab;

import android.view.View;
import android.view.ViewGroup;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.view.ViewHelper;
import es.jcyl.ita.frmdrd.view.render.BaseGroupRenderer;
import es.jcyl.ita.frmdrd.view.render.GroupRenderer;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

public class TabItemRenderer extends BaseGroupRenderer<UITabItem> implements GroupRenderer<UITabItem> {
    @Override
    protected ViewGroup createBaseView(RenderingEnv env, UITabItem component) {
        ViewGroup view = ViewHelper.inflate(env.getViewContext(), R.layout.fragment_tab, ViewGroup.class);
        view.setTag(component.getLabel());
        return view;
    }

    @Override
    protected void setupView(RenderingEnv env, ViewGroup baseView, UITabItem component) {

    }

    @Override
    public void addViews(RenderingEnv env, UITabItem component, ViewGroup root, View[] views) {
        for (View view : views) {
            root.addView(view);
        }
    }
}
