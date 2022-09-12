package es.jcyl.ita.formic.app.dialog;/*
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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.R;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public class ProgressDialog extends Dialog{
    public Activity activity;
    private ProgressBar mProgressBar;
    private MaterialButton backButton;
    private MaterialButton showConsoleButton;
    private TextView progressText;
    private TextView progressTitle;
    private EditText progressConsole;
    private StringBuilder builder;
    private LinearLayout progressConsoleLayout;
    private ListView listView;

    List<String> listItems;
    List<String> listStrItems;

    public ProgressDialog(Activity activity){
        super(activity, R.style.DialogStyle);
        this.activity = activity;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.layout_loading_dialog);


        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        backButton = (MaterialButton) findViewById(R.id.back_button);
        showConsoleButton = (com.google.android.material.button.MaterialButton) findViewById(R.id.show_console_button);
        progressText = (TextView) findViewById(R.id.text_loading_dialog);
        progressTitle = (TextView) findViewById(R.id.progress_title);
        progressConsole = findViewById(R.id.progress_console);
        progressConsoleLayout = findViewById(R.id.progress_console_layout);
        //progressResourcesLayout = findViewById(R.id.progress_resources_layout);
        listView = findViewById(R.id.progress_list_view);

        listItems= new ArrayList<>();
        listStrItems= new ArrayList<>();

        builder = new StringBuilder();


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                activity.finish();
            }
        });

        showConsoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressConsole.setVisibility(progressConsole.getVisibility() == View.VISIBLE?View.GONE:View.VISIBLE);


                Drawable img = getContext().getResources().getDrawable(progressConsole.getVisibility() == View.VISIBLE?R.drawable.ic_action_collapse:R.drawable.ic_action_expand);

                // set new drawable
                showConsoleButton.setIcon(img);
       }
        });
    }

    public void setConsoleText(String message){
        builder.append(message);
        builder.append("\n");
    }

    public void setText(String message){
        TextView progressText = (TextView) findViewById(R.id.text_loading_dialog);
        progressText.setText("" + message);
        progressText.setVisibility(View.VISIBLE);
        setConsoleText(message);
    }

    public void showProgressButton() {
        setText(activity.getString(R.string.process_finish));
        mProgressBar.setVisibility(View.GONE);
        progressConsole.setText(builder, TextView.BufferType.SPANNABLE);
        progressConsoleLayout.setVisibility(View.VISIBLE);
        progressTitle.setText("GENERAR ACTA");
        progressTitle.setVisibility(View.VISIBLE);
        showListResources();

    }

    public void addResource(String resourcePath) {
        listItems.add(resourcePath);
        listStrItems.add(new File(resourcePath).getName());
    }

    private void showListResources() {

        ArrayAdapter<String> adapter;
        adapter=new ArrayAdapter<String>(activity, R.layout.progress_dialog_item, R.id.progress_dialog_item_id, listStrItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final File file = new File(listItems.get(position));

                final Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", file);

                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                intentShareFile.setType("application/msword");
                intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
                activity.startActivity(Intent.createChooser(intentShareFile, "Share File"));
            }
        });
    }

}