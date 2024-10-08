package es.jcyl.ita.formic.app.settings;/*
 * Copyright 2020 Gustavo Río (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
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
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.google.android.material.textfield.TextInputLayout;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.app.MainActivity;
import es.jcyl.ita.formic.forms.util.FileUtils;
import es.jcyl.ita.formic.forms.view.activities.BaseActivity;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public class SettingsActivity extends BaseActivity {

    private EditText pathEditText;
    private TextInputLayout textInputLayout;

    private static final int TREE_REQUEST_CODE = 9999;

    @Override
    protected void doOnCreate() {
        setContentView(R.layout.activity_settings);
        setToolbar(getString(R.string.action_settings));
        setCurrentWorkspace();

    }

    private void setCurrentWorkspace(){
        pathEditText = findViewById(R.id.path_text);
        pathEditText.setTag(Boolean.TRUE);
        pathEditText.setText(getCurrentWorkspace());

        textInputLayout = findViewById(R.id.projects_folder);
        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(Intent.createChooser(intent
                        , getString(R.string.choose_directory))
                        , TREE_REQUEST_CODE);
            }
        });

    }

    public String setCurrentWorkspace(String path) {
        sharedPreferences.edit().putString("current_workspace", path).apply();
        currentWorkspace = sharedPreferences.getString("current_workspace", getExternalFilesDir(null).getAbsolutePath() + "/projects");
        return path;
    }

    public String getCurrentWorkspace() {
        currentWorkspace = sharedPreferences.getString("current_workspace", getExternalFilesDir(null).getAbsolutePath() + "/projects");
        return currentWorkspace;
    }

    @SuppressLint("MissingSuperCall")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TREE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
            }
            if (uri != null) {
                final String path = FileUtils.getPath(this, uri);
                if (path != null) {
                    pathEditText.setText(setCurrentWorkspace(path));
                    pathEditText.setTag(Boolean.FALSE);
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

}
