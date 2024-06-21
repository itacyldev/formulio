package es.jcyl.ita.formic.forms.components.bottomsheet;/*
 * Copyright 2020 Rosa María Muñiz (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.dialog.BottomSheetListView;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {

 private ProgressBar progressBar;
 private TextView textView;
 private BottomSheetListView listView;

 private int progressStatus = 0;
 private Handler handler = new Handler();
 private List<String> lstResources;

 public static CustomBottomSheetDialogFragment newInstance() {
  CustomBottomSheetDialogFragment fragment = new CustomBottomSheetDialogFragment();
  return fragment;
 }

 @Override
 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
  View rootView = inflater.inflate(R.layout.content_bottom_sheet, container, false);

  progressBar = rootView.findViewById(R.id.progressBar);
  textView = rootView.findViewById(R.id.textView);
  listView = rootView.findViewById(R.id.lsViewBtmSheet);

  List<String> listItems= new ArrayList<>();
  listItems.add("Uno.docx");
  listItems.add("Dos.docx");
  setLstResources(listItems);

  progressBar.setVisibility(ProgressBar.VISIBLE);

  setProgressValue(rootView.getContext(), progressStatus);

  return rootView;
 }

 private void setProgressValue(Context context, final int progress) {

  // set the progress
  progressBar.setProgress(progress);
  // thread is used to change the progress value
  new Thread(new Runnable() {
   public void run() {
    while (progressStatus < 100) {
     progressStatus += 1;
     handler.post(new Runnable() {
      public void run() {
       progressBar.setProgress(progressStatus);
       textView.setText(progressStatus + "/" + progressBar.getMax());
       if (progressStatus == 100) {
        textView.setText(" Your Progess has been Completed");
        showListResources(context);
       }
      }
     });
     try {
      // Sleep for 50 ms to show progress you can change it as well.
      Thread.sleep(50);
     } catch (InterruptedException e) {
      e.printStackTrace();
     }
    }
   }
  }).start();
 }

 private void showListResources(Context context) {

  ArrayAdapter<String> adapter;
  adapter=new ArrayAdapter<String>(context, es.jcyl.ita.formic.forms.R.layout.botton_sheet_item, es.jcyl.ita.formic.forms.R.id.bottom_sheet_item_id, getLstResources());
  listView.setAdapter(adapter);

  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
   @Override
   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    String projectsFolder = sharedPreferences.getString("current_workspace", context.getExternalFilesDir(null).getAbsolutePath() + "/projects");
    final File file = new File(projectsFolder+"/docs/"+getLstResources().get(position));

    final Uri uri = FileProvider.getUriForFile(view.getContext(),
            view.getContext().getPackageName() + "" +
                    ".provider", file);

    final Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("application/msword");
    intent.putExtra(Intent.EXTRA_STREAM, uri);
    startActivity(intent);
   }
  });
 }

 public List<String> getLstResources() {
  return lstResources;
 }

 public void setLstResources(List<String> lstResources) {
  this.lstResources = lstResources;
 }

}