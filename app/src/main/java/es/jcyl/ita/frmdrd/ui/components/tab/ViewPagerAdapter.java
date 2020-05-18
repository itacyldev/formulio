package es.jcyl.ita.frmdrd.ui.components.tab;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.frmdrd.R;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private List<TabFragment> tabFragments = new ArrayList<>();
    ClassLoader classLoader;

    public ViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        classLoader = fragmentActivity.getClassLoader();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        FragmentFactory factory = new FragmentFactory();
        //TabFragment fragment = (TabFragment)factory.instantiate(classLoader, TabFragment.class.getName());
        TabFragment fragment = TabFragment.newInstance(position);

        tabFragments.add(fragment);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return tabFragments.size();
    }

    public void addView(View view, int position, FragmentActivity fragmentActivity) {
        TabFragment fragment = (TabFragment) createFragment(position);

//        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
//        fm.beginTransaction().add(R.id.body_content, fragment).commit();

        fragment.setTabView(view);


        this.notifyDataSetChanged();
    }
}

