package es.jcyl.ita.frmdrd.ui.components.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import es.jcyl.ita.frmdrd.R;

public class TabFragment extends Fragment {

    private String title;

    private View tabView;
    private static final String ARG_COUNT = "param1";

    public static TabFragment newInstance(int position) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COUNT, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_tab, container, false);
        if (tabView != null) {
            view.addView(tabView);
        }
        return view;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTabView(View view) {
        tabView = view;
    }

    public View getTabView() {
        return tabView;
    }
}
