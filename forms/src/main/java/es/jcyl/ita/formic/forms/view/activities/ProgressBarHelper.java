package es.jcyl.ita.formic.forms.view.activities;
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
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;

import es.jcyl.ita.formic.forms.R;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class ProgressBarHelper {

    private Dialog dialog;
    private int delayMs = 200;

    private boolean open;

    public ProgressBarHelper(Context context) {
        // create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context, es.jcyl.ita.formic.forms.R.style.DialogStyle);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.dialog_loading);
        dialog = builder.create();
    }

    public void show() {
        open = true;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (open) {
                    dialog.show();
                }
            }
        }, this.delayMs);
    }

    public void hide() {
        open = false;
        dialog.dismiss();
    }

    public int getDelayMs() {
        return delayMs;
    }

    public void setDelayMs(int delayMs) {
        this.delayMs = delayMs;
    }
}
