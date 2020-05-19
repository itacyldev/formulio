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

    private List<View> tabContents = new ArrayList<>();
    ClassLoader classLoader;

    public ViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        classLoader = fragmentActivity.getClassLoader();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //TabFragment fragment = TabFragment.newInstance(position);
        return tabFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return tabFragments.size();
    }

    public void addView(View view, int position, FragmentActivity fragmentActivity) {
        TabFragment fragment = TabFragment.newInstance(position);
        tabFragments.add(fragment);
//        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
//        fm.beginTransaction().add(R.id.body_content, fragment).commit();

        fragment.setTabView(view);


       this.notifyDataSetChanged();
    }
}

