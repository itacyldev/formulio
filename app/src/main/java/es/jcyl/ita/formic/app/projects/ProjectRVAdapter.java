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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.project.Project;

/**
 * @author José Ramón Cuevas (joseramon.cuevas@itacyl.es)
 */
public class ProjectRVAdapter extends RecyclerView.Adapter<ProjectRVAdapter.ViewHolder> {
    private static final String TAG = "ProjectAdapter";

    private static List<Project> projectList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView project_nameTextView;
        private final TextView project_descriptionTextView;

        public ViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = project_nameTextView.getContext();
                    // TODO: extract Project View Helper to FORMIC-27
                    Project prj = projectList.get(getAdapterPosition());
                    Toast.makeText(context,
                            DevConsole.info(context.getString(R.string.project_opening_init,
                                    (String) prj.getId())),
                            Toast.LENGTH_LONG).show();
                    try {
                        Config.getInstance().setCurrentProject(prj);
                        Toast.makeText(context,
                                DevConsole.info(context.getString(R.string.project_opening_finish, (String) prj.getId())),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context,
                                DevConsole.info(context.getString(R.string.project_opening_error, (String) prj.getId())),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            project_nameTextView = (TextView) itemView.findViewById(R.id.projectName);
            project_descriptionTextView = (TextView) itemView.findViewById(R.id.projectDescription);
        }

        public TextView getProject_nameTextView(){
            return project_nameTextView;
        }

        public TextView getProject_descriptionTextView(){
            return project_descriptionTextView;
        }
    }

    public ProjectRVAdapter(List<Project> project_list){
        projectList = project_list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.project_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position){
        Project project = projectList.get(position);
        viewHolder.getProject_nameTextView().setText(project.getName());
        viewHolder.getProject_descriptionTextView().setText(project.getDescription());
    }

    @Override
    public int getItemCount(){
        return projectList.size();
    }
}