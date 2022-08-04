package es.jcyl.ita.formic.app.jobs;
/*
 * Copyright 2022 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.forms.view.activities.BaseActivity;
import es.jcyl.ita.formic.jayjobs.jobs.exec.JobExecInMemo;
import util.Log;

/**
 *
 * Activity to show the execution of a job and its results
 *
 * @autor Javier Ramos (javier.ramos@itacyl.es)
 */
public class JobProgressActivity extends BaseActivity {
    private final Activity activity = this;

    private ProgressBar progressBar;
    private JobExecStatusListener execStatusListener;

    private JobProgressHandler mainThreadHandler;

    public JobProgressHandler getMainThreadHandler() {
        return mainThreadHandler;
    }

    @Override
    protected void doOnCreate() {
        setContentView(R.layout.job_progress);
        long jobId = getIntent().getLongExtra("jobExecId", -1);
        JobExecInMemo jobExecRepo = JobExecInMemo.getInstance();
        execStatusListener = new JobExecStatusListener(this, jobId, jobExecRepo);

        //setToolbar(getString(R.string.action_settings));
        progressBar = (ProgressBar) findViewById(R.id.progressBar_cyclic);


        Button button = (Button) findViewById(R.id.progress_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        mainThreadHandler = new JobProgressHandler();

        execStatusListener.startActiveWaiting();
    }

    public void setMessage(String end, String msg) {
        LinearLayout layout = findViewById(R.id.progress_messages_layout);
        TextView view = new TextView(this);
        view.setId((int)System.currentTimeMillis());
        view.setText(msg);
        layout.addView(view);
    }

    public void setResources(List<String> resources) {

    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    public void endJob(){
        Button button = findViewById(R.id.progress_button);
        button.setEnabled(true);
    }


    private class JobProgressHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == JobExecStatusListener.MSG_CODE) {
                Bundle msgData = msg.getData();
                String msgText = msgData.getString("msgTxt");
                setMessage("", msgText);
            }

        }
    }

}
