package es.jcyl.ita.frmdrd.ui.components.tab;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private List<TabFragment> tabFragments = new ArrayList<>();


    public ViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
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
        fragment.setTabView(view);
        this.notifyDataSetChanged();
    }

    public TabFragment getFragmentAt(int position) {
        if (tabFragments.size() > position)
            return tabFragments.get(position);
        else
            return null;
    }
}

