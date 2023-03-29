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
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.forms.util.FileUtils;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public class JobResultDialog extends Dialog{
    public Activity activity;
    private ProgressBar mProgressBar;
    private MaterialButton acceptButton;
    private MaterialButton showConsoleButton;

    private ImageView closeButton;

    private TextView progressText;
    private TextView progressTitle;
    private EditText progressConsole;
    private StringBuilder builder;
    private LinearLayout progressConsoleLayout;
    private ListView listView;

    List<String> listItems;
    List<String> listStrItems;

    boolean finishActivity = true;

    private static Map<String, Drawable> icons = new HashMap<String, Drawable>();

    public JobResultDialog(Activity activity, boolean finishActivity){
        super(activity, R.style.DialogStyle);
        this.activity = activity;
        this.finishActivity = finishActivity;
    }

   public MaterialButton getAcceptButton() {
        return acceptButton;
    }

    public MaterialButton getShowConsoleButton() {
        return showConsoleButton;
    }

    public ImageView getCloseButton() {
        return closeButton;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.job_progress_dialog);
        

        setWidthAndHeight();

        setCanceledOnTouchOutside(false);

        mProgressBar = findViewById(R.id.progress_bar);
        acceptButton = findViewById(R.id.accept_button);
        showConsoleButton = findViewById(R.id.show_console_button);

        closeButton = findViewById(R.id.close_button);

        progressText = findViewById(R.id.text_loading_dialog);
        progressText.setMovementMethod(new ScrollingMovementMethod());
        progressTitle = findViewById(R.id.progress_title);
        progressConsole = findViewById(R.id.progress_console);
        progressConsole.setMovementMethod(new ScrollingMovementMethod());
        progressConsoleLayout = findViewById(R.id.progress_console_layout);
        listView = findViewById(R.id.progress_list_view);

        listItems= new ArrayList<>();
        listStrItems= new ArrayList<>();

        builder = new StringBuilder();


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (finishActivity) {
                    activity.finish();
                }
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

    public void setProgressTitle(String title){
        progressTitle.setText(StringUtils.upperCase(title));
    }

    public void setConsoleText(String message){
        builder.append(message);
        builder.append("\n");
    }

    public void setText(String message){
        progressText.setText("" + message);
        progressText.setVisibility(View.VISIBLE);
        setConsoleText(message);
        progressConsole.setText(builder, TextView.BufferType.SPANNABLE);
    }

    public void endJob() {
        setText(activity.getString(R.string.process_finish));
        mProgressBar.setVisibility(View.GONE);
        progressConsole.setText(builder, TextView.BufferType.SPANNABLE);
        closeButton.setVisibility(View.VISIBLE);
        showListResources();

    }

    public void addResource(String resourcePath) {
        listItems.add(resourcePath);
        listStrItems.add(new File(resourcePath).getName());
    }

    private void showListResources() {

        listView.setVisibility(View.VISIBLE);
        listView.setAdapter(new ResourceAdapter(activity, listStrItems));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final File file = new File(listItems.get(position));

                final Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", file);

                Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                final String fileType = FileUtils
                        .getFileType(file);
                intentShareFile.setType(fileType);
                intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
                activity.startActivity(Intent.createChooser(intentShareFile, "Share File"));
            }
        });
    }

    private void setWidthAndHeight(){

        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.60);
        this.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private class ResourceAdapter extends ArrayAdapter<String> {

        public ResourceAdapter(Context context, final List<String> lst) {
            super(context, 0, lst);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewHolder holder;

            if (null == convertView) {
                convertView = inflater.inflate(
                        R.layout.job_progress_dialog_item,
                        parent,
                        false);

                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.dialog_resource_image);
                holder.textView = (TextView) convertView.findViewById(R.id.dialog_resource_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Lead actual.
            String resource = (String) getItem(position);

            // Setup.
            holder.textView.setText(resource);
            if (IconFactory.getInstance().getIcon(FilenameUtils.getExtension(resource)) != null) {
                Drawable drawable = ContextCompat.getDrawable(getContext(), IconFactory.getInstance().getIcon(FilenameUtils.getExtension(resource)).intValue());
                holder.imageView.setImageDrawable(drawable);
            }

            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }

   }

}