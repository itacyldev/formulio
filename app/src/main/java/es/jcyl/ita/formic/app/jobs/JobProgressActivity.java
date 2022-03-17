package es.jcyl.ita.formic.app.jobs;
/*
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

import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.forms.view.activities.BaseActivity;

/**
 *
 */
public class JobProgressActivity extends BaseActivity {
    private ProgressBar progressBar;
    private TextView textView;
    private static JobProgressListener progressListener;

    @Override
    protected void doOnCreate() {
        setContentView(R.layout.job_progress);
        progressListener = (JobProgressListener) getIntent().getSerializableExtra("jobListener");
        progressListener.setActivity(this);
        setToolbar(getString(R.string.action_settings));
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);
    }

    public void setMessage(String end, String msg) {
        CharSequence text = textView.getText();
        textView.setText(text + "\n" + msg);
    }

    public void setResources(List<String> resources){

    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

}
