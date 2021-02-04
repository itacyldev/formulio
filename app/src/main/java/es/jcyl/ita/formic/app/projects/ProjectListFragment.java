package es.jcyl.ita.formic.app.projects;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectRepository;

/**
 * @author José Ramón Cuevas (joseramon.cuevas@itacyl.es)
 */
public class ProjectListFragment extends Fragment {

    private static final String TAG = "ProjectListFragment";

    public static final String PROJECT_LIST = "PROJECT_LIST";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            ((Activity)context).setTitle(getString(R.string.projects_of) + getString(R.string.app_name));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.project_list_fragment, container, false);
        rootView.setTag(TAG);

        RecyclerView mReciclerView = (RecyclerView) rootView.findViewById(R.id.rvProjects);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mReciclerView.setLayoutManager(mLayoutManager);
        mReciclerView.scrollToPosition(0);
        Bundle arguments = getArguments();
        ArrayList<Project> projects = (arguments != null)?
                (ArrayList<Project>)arguments.getSerializable(PROJECT_LIST)
                : new ArrayList<>();
        ProjectRVAdapter mAdapter = new ProjectRVAdapter(projects);
        mReciclerView.setAdapter(mAdapter);

        if (projects.isEmpty()){
            Toast.makeText(getContext(), getString(R.string.no_projects),
                    Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }

    public static ProjectListFragment newInstance(@NonNull final ProjectRepository projectRepository){
        final Bundle bundle = new Bundle();
        bundle.putSerializable(PROJECT_LIST, new ArrayList<>(projectRepository.listAll()));

        ProjectListFragment fragment = new ProjectListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
