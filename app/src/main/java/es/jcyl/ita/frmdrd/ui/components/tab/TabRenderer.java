package es.jcyl.ita.frmdrd.ui.components.tab;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.view.render.BaseGroupRenderer;
import es.jcyl.ita.frmdrd.view.render.GroupRenderer;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;


public class TabRenderer extends BaseGroupRenderer<UITab> implements GroupRenderer<UITab> {

    protected ViewGroup createBaseView(RenderingEnv env, UITab component) {
        ViewGroup layout = (ViewGroup) View.inflate(env.getViewContext(),
                R.layout.component_tab, null);
        return layout;
    }

    @Override
    protected void setupView(RenderingEnv env, ViewGroup baseView, UITab component) {
        TabLayout tabLayout = baseView.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = baseView.findViewById(R.id.viewPager);
        //        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter();
//        viewPager.setAdapter(viewPagerAdapter);
//
//        tabLayout.

    }

    @Override
    public void addViews(RenderingEnv env, UITab component, ViewGroup root, View[] views) {
        TabLayout tabLayout = root.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = root.findViewById(R.id.viewPager);

        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();

        int fragCount = 0;
        for (View view : views) {
            tabLayout.addTab(tabLayout.newTab());
            adapter.createFragment(fragCount);
            adapter.notifyDataSetChanged();

            fragCount++;
        }
    }

}
