package es.jcyl.ita.formic.app.workspace;/*
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
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.forms.util.FileUtils;
import es.jcyl.ita.formic.forms.view.activities.BaseActivity;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public class WorkspaceActivity extends BaseActivity {

    private EditText pathEditText;
    private Button pathImageButton;

    private static final int TREE_REQUEST_CODE = 9999;

    @Override
    protected void doOnCreate() {
        setContentView(R.layout.activity_workspace);
        setToolbar(getString(R.string.action_workspace));
        //setCurrentWorkspace();

        pathEditText = (EditText) findViewById(R.id.path_text);
        pathEditText.setTag(Boolean.TRUE);
        pathEditText.setText(setCurrentWorkspace(Environment.getExternalStorageDirectory().getAbsolutePath() + "/projects"));

        pathImageButton = (Button) findViewById(R.id.path_button);
        pathImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(Intent.createChooser(intent
                        , getString(R.string.choose_directory))
                        , TREE_REQUEST_CODE);
            }
        });

    }

    /*private void setCurrentWorkspace(){
        TextView labelCurrentWorkspace = findViewById(R.id.label_current_workspace);
        setDefaultCurrentWorkspace();
        labelCurrentWorkspace.setText(sharedPreferences.getString("current_workspace", Environment.getExternalStorageDirectory().getAbsolutePath() + "/projects"));
    }*/

    private String setCurrentWorkspace(String path) {
        sharedPreferences.edit().putString("current_workspace", path).apply();
        return path;
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
                }
            }
        }
    }

}
