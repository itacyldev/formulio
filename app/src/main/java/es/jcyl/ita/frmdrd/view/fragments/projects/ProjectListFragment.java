package es.jcyl.ita.frmdrd.view.fragments.projects;/*
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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.config.Config;
import es.jcyl.ita.frmdrd.project.Project;
import es.jcyl.ita.frmdrd.view.activities.FormListFragment;

/**
 * @author José Ramón Cuevas (joseramon.cuevas@itacyl.es)
 */
public class ProjectListFragment extends Fragment {

    private static final String TAG = "ProjectRVFragment";

    private List<Project> projectList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            ((Activity)context).setTitle(R.string.app_name);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        loadProjects();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.project_list_fragment, container, false);
        rootView.setTag(TAG);

        RecyclerView mReciclerView = (RecyclerView) rootView.findViewById(R.id.rvProjects);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mReciclerView.setLayoutManager(mLayoutManager);
        mReciclerView.scrollToPosition(0);

        ProjectRVAdapter mAdapter = new ProjectRVAdapter(projectList);
        mReciclerView.setAdapter(mAdapter);
        return rootView;
    }

    private void loadProjects(){
        projectList = Config.getInstance().getProjectRepo().listAll();
    }
}
