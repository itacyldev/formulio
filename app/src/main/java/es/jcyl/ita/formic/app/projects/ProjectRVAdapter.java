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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.app.MainActivity;
import es.jcyl.ita.formic.app.dialog.JobResultDialog;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectImporter;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;
import es.jcyl.ita.formic.jayjobs.task.utils.ContextAccessor;

/**
 * @author José Ramón Cuevas (joseramon.cuevas@itacyl.es)
 */
public class ProjectRVAdapter extends RecyclerView.Adapter<ProjectRVAdapter.ViewHolder> {
    private static final String TAG = "ProjectAdapter";

    private static List<Project> projectList = new ArrayList<>();

    private static Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView project_nameTextView;
        private final TextView project_descriptionTextView;
        private final ImageButton buttonViewOption;
        int count = 1;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    context = project_nameTextView.getContext();
                    new MyTask(context).execute(10);

                }
            });

            project_nameTextView = (TextView) itemView.findViewById(R.id.projectName);
            project_descriptionTextView = (TextView) itemView.findViewById(R.id.projectDescription);

            buttonViewOption = itemView.findViewById(R.id.item_project_options);

        }

        public TextView getProject_nameTextView() {
            return project_nameTextView;
        }

        public TextView getProject_descriptionTextView() {
            return project_descriptionTextView;
        }

        public ImageButton getButtonViewOption() {
            return buttonViewOption;
        }

        class MyTask extends AsyncTask<Integer, Integer, String> {
            AlertDialog dialog;
            boolean projectOpeningFinish = true;
            Project prj;
            Context currentContext;

            public MyTask(Context context) {
                currentContext =  context;
            }

            @Override
            protected String doInBackground(Integer... params) {
                // TODO: extract Project View Helper to FORMIC-27
                prj = projectList.get(getAdapterPosition());
                //String projectsFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/projects";
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(currentContext);
                String projectsFolder = sharedPreferences.getString("current_workspace", currentContext.getExternalFilesDir(null).getAbsolutePath() + "/projects");
                DevConsole.setLogFileName(projectsFolder, (String) prj.getId());

                App.getInstance().setCurrentProject(prj);

                return "Task Completed.";
            }
            @Override
            protected void onPostExecute(String result) {
                try {
                    ((MainActivity) currentContext).loadFragment();
                    ((MainActivity) currentContext).loadImageNoProjects();
                } catch (Exception e) {
                    projectOpeningFinish = false;

                }
                dialog.dismiss(); // to hide this dialog
                if (!projectOpeningFinish){
                    UserMessagesHelper.toast(currentContext,
                            DevConsole.info(currentContext.getString(R.string.project_opening_error, (String) prj.getId())),
                            Toast.LENGTH_LONG);
                }else{
                    UserMessagesHelper.toast(currentContext,
                            DevConsole.info(currentContext.getString(R.string.project_opening_finish, (String) prj.getId())),
                            Toast.LENGTH_LONG);
                }
            }
            @Override
            protected void onPreExecute() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, es.jcyl.ita.formic.forms.R.style.DialogStyle);
                builder.setCancelable(false); // if you want user to wait for some process to finish,
                builder.setView(R.layout.layout_loading_dialog);
                dialog = builder.create();
                dialog.show(); // to show this dialog

            }
            @Override
            protected void onProgressUpdate(Integer... values) {
                // Do nothing
            }
        }
    }

    public ProjectRVAdapter(List<Project> project_list, Context ctx) {
        projectList = project_list;
        context = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.project_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Project project = projectList.get(position);
        viewHolder.getProject_nameTextView().setText(project.getName());
        viewHolder.getProject_descriptionTextView().setText(project.getDescription());

        viewHolder.getButtonViewOption().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                ContextThemeWrapper ctw = new ContextThemeWrapper(context, R.style.ActionBarPopupStyle);
                PopupMenu popup = new PopupMenu(ctw, viewHolder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_project_item);

                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ZipTask task = new ZipTask();
                        task.execute((String)viewHolder.getProject_nameTextView().getText());

                        return false;
                    }
                });
                //displaying the popup
                popup.show();


            }
        });
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    /*private class ZipTask extends AsyncTask<String, String, String> {
        AlertDialog dialog;

        protected String doInBackground(final String... params) {

            String dest = ContextAccessor.workingFolder(App.getInstance().getGlobalContext());
            new File(dest).mkdirs();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String projectsFolder = sharedPreferences.getString("current_workspace", context.getExternalFilesDir(null).getAbsolutePath() + "/projects");

            ProjectImporter projectImporter = ProjectImporter.getInstance();
            File file = projectImporter.zipFolder(new File(projectsFolder), params[0],  new File(dest));
            shareFile(file);

            return "";
        }

        private void shareFile(File file) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType(URLConnection.guessContentTypeFromName(file.getName()));
            shareIntent.putExtra(Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file));
            //shareIntent.setType("application/zip");
            context.startActivity(Intent.createChooser(shareIntent, "Share File"));
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(final String success) {
            dialog.dismiss(); // to hide this dialog
            if (success.isEmpty()) {
                UserMessagesHelper.toast(context, "Export successful!", Toast.LENGTH_SHORT);
            } else {
                UserMessagesHelper.toast(context, "Export failed!", Toast.LENGTH_SHORT);
            }
        }
        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, es.jcyl.ita.formic.forms.R.style.DialogStyle);
            builder.setCancelable(false); // if you want user to wait for some process to finish,
            builder.setView(R.layout.layout_loading_dialog);
            dialog = builder.create();
            dialog.show(); // to show this dialog

        }
    }*/

    private class ZipTask extends AsyncTask<String, String, String> {
        JobResultDialog jobResultDialog;

        protected String doInBackground(final String... params) {

            String dest = ContextAccessor.workingFolder(App.getInstance().getGlobalContext());
            new File(dest).mkdirs();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String projectsFolder = sharedPreferences.getString("current_workspace", context.getExternalFilesDir(null).getAbsolutePath() + "/projects");

            ProjectImporter projectImporter = ProjectImporter.getInstance();
            File file = projectImporter.zipFolder(new File(projectsFolder), params[0],  new File(dest));
            jobResultDialog.addResource(file.getPath());

            return "";
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(final String success) {
            if (success.isEmpty()) {
                jobResultDialog.setText("Export successful!");
                UserMessagesHelper.toast(context, "Export successful!", Toast.LENGTH_SHORT);
            } else {
                jobResultDialog.setText("Export failed!");
                UserMessagesHelper.toast(context, "Export failed!", Toast.LENGTH_SHORT);
            }
            jobResultDialog.endJob();
            jobResultDialog.getAcceptButton().setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPreExecute() {
            jobResultDialog = new JobResultDialog((MainActivity) context, false);
            jobResultDialog.show();
            jobResultDialog.setProgressTitle(context.getString(R.string.export));
            jobResultDialog.setText(context.getString(R.string.exporting));
            jobResultDialog.getBackButton().setVisibility(View.GONE);

            jobResultDialog.getAcceptButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jobResultDialog.dismiss();
                }
            });
        }
    }

}
