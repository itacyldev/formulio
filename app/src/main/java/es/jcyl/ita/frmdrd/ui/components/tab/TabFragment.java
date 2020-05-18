package es.jcyl.ita.frmdrd.ui.components.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.apache.commons.lang3.RandomUtils;

import es.jcyl.ita.frmdrd.R;

public class TabFragment extends Fragment {

    private String title;

    private View tabView;
    private static final String ARG_COUNT = "param1";
    private Integer counter;


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
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
//        ViewGroup contentView = view.findViewById(R.id.content_view);
//        if (tabView != null) {
//            contentView.addView(tabView);
//        }
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textViewCounter = view.findViewById(R.id.content_view);
        textViewCounter.setText("Fragment No " + counter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            counter = getArguments().getInt(ARG_COUNT);
        }
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

}
