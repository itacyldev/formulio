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

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.app.MainActivity;
import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.view.UserMessagesHelper;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class ProjectHelper {

 public static void openProject(Context context, Project project) {
    new MyTask(context, project).execute(10);

  /*MyTask myTask = new MyTask(context, project);
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
   myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 10);
  } else {
   myTask.execute(10);
  }*/

 }

 static class MyTask extends AsyncTask<Integer, Integer, String> {
  AlertDialog dialog;
  boolean projectOpeningFinish = true;
  Project prj;
  Context currentContext;

  public MyTask(Context context, Project project) {
   currentContext = context;
   prj = project;
  }

  @Override
  protected String doInBackground(Integer... params) {
   // TODO: extract Project View Helper to FORMIC-27
   //String projectsFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/projects";
   SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(currentContext);
   String projectsFolder = sharedPreferences.getString("current_workspace", currentContext.getExternalFilesDir(null).getAbsolutePath() + "/projects");
   DevConsole.setLogFileName(projectsFolder, (String) prj.getId());
   try {
    sharedPreferences.edit().putString("projectName", prj.getName()).apply();
    App.getInstance().openProject(prj);
   } catch (Exception e) {
    DevConsole.error("Error while trying to open project " + prj.getName(), e);
    return "error";
   }
   return "ok";
  }

  @Override
  protected void onPostExecute(String result) {
   if (!result.equals("error")) {
    try {
     ((MainActivity) currentContext).loadFragment();
     ((MainActivity) currentContext).loadImageNoProjects();
    } catch (Exception e) {
     projectOpeningFinish = false;
    }
   }
   dialog.dismiss(); // to hide this dialog
   if (!projectOpeningFinish || result.equals("error")) {
    UserMessagesHelper.toast(currentContext,
            DevConsole.info(currentContext.getString(R.string.project_opening_error, (String) prj.getId())),
            Toast.LENGTH_LONG);
   } else {
    UserMessagesHelper.toast(currentContext,
            DevConsole.info(currentContext.getString(R.string.project_opening_finish, (String) prj.getId())),
            Toast.LENGTH_LONG);
   }
  }

  @Override
  protected void onPreExecute() {
   AlertDialog.Builder builder = new AlertDialog.Builder(currentContext, es.jcyl.ita.formic.forms.R.style.DialogStyle);
   builder.setCancelable(false); // if you want user to wait for some process to finish,
   builder.setView(R.layout.dialog_layout_loading);
   dialog = builder.create();
   dialog.show(); // to show this dialog
  }

  @Override
  protected void onProgressUpdate(Integer... values) {
   // Do nothing
  }
 }
}


