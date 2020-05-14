package es.jcyl.ita.frmdrd.ui.components.tab;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import org.apache.commons.lang3.RandomUtils;

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

        TabFragment fragment = new TabFragment();
        FragmentTransaction ft = ((AppCompatActivity) env.getViewContext()).getSupportFragmentManager().beginTransaction();
        ft.add(RandomUtils.nextInt(), fragment).commit();

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragment);
        viewPager.setAdapter(viewPagerAdapter);
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
            TabFragment fragment = adapter.getFragment(fragCount);

            FragmentTransaction ft = ((AppCompatActivity) env.getViewContext()).getSupportFragmentManager().beginTransaction();
            ft.add(R.id.tab_content, fragment).commit();

            ((LinearLayout) fragment.getView()).addView(view);
            adapter.notifyDataSetChanged();

            fragCount++;
        }
    }

}
