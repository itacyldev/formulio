package es.jcyl.ita.frmdrd.ui.components.tab;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private List<TabFragment> tabFragments = new ArrayList<>();

    public ViewPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        TabFragment fragment = new TabFragment();
        tabFragments.add(fragment);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return tabFragments.size();
    }

    public TabFragment getFragment(int position){
        return tabFragments.get(position);
    }
}

