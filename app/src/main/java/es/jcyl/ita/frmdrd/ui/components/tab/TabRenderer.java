package es.jcyl.ita.frmdrd.ui.components.tab;

import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTabHost;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
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
//        for (int i = 0; i < component.getChildren().length; i++) {
//            FragmentManager fm = ((AppCompatActivity) env.getViewContext()).getSupportFragmentManager();
//            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fm);
//            viewPager.setAdapter(viewPagerAdapter);
//        }
    }

    @Override
    public void addViews(RenderingEnv env, UITab component, ViewGroup root, View[] views) {
        TabLayout tabLayout =  ((AppCompatActivity) env.getViewContext()).findViewById(R.id.tab_layout);
        ViewPager2 viewPager = ((AppCompatActivity) env.getViewContext()).findViewById(R.id.viewPager);



        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fm);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        int fragCount = 0;
        for (View view : views) {
            tabLayout.addTab(tabLayout.newTab().setText(view.getTag().toString()));

            FragmentTransaction fragTransaction = fm.beginTransaction();
            TabFragment tabFragment = new TabFragment();
            fragTransaction.add(R.id.tab_content, tabFragment, "fragment" + fragCount);
            fragTransaction.commit();

            tabFragment.setTitle(view.getTag().toString());
            viewPagerAdapter.addFragment(tabFragment);
            viewPagerAdapter.notifyDataSetChanged();

            tabFragment.addContent(view);

            fragCount++;
        }
    }

}
