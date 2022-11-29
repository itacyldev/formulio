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

import es.jcyl.ita.formic.forms.R;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class WaitingLayerHelper {


    private static boolean open;

    public  static Dialog createDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, es.jcyl.ita.formic.forms.R.style.DialogStyle);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.dialog_loading);
        Dialog dialog = builder.create();
        return dialog;
    }
    public static void showWithDelay(Dialog dialog, int ms){
        try {
            open = true;
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
        if(open){
            dialog.show();
        }
    }
    public static void dismiss(Dialog dialog){
        open = false;
        dialog.dismiss();
    }
}
