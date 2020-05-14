package es.jcyl.ita.frmdrd.ui.components.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import es.jcyl.ita.frmdrd.R;

public class TabFragment extends Fragment {

    private String title;

    private LinearLayout contentView;

    ViewPagerAdapter viewPagerAdapter;
    ViewPager viewPager;

    public TabFragment(){
        super();
    }



    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        contentView = view.findViewById(R.id.tab_content);
    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_tab, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
//        viewPager = view.findViewById(R.id.viewPager);
//        viewPager.setAdapter(viewPagerAdapter);
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addContent(View view) {
        contentView.addView(view);
    }
}
