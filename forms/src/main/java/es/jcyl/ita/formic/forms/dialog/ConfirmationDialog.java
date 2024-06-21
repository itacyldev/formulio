package es.jcyl.ita.formic.forms.dialog;/*
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


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import es.jcyl.ita.formic.forms.R;


/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public class ConfirmationDialog extends Dialog{
    public Context context;
    private TextView confirmationDialogTitle;
    private TextView confirmationDialogText;
    private MaterialButton acceptButton;
    private MaterialButton cancelButton;


    public ConfirmationDialog(Context ctx){
        super(ctx, R.style.DialogStyle);
        this.context = ctx;
    }

    public MaterialButton getCancelButton() {
        return cancelButton;
    }

    public MaterialButton getAcceptButton() {
        return acceptButton;
    }

    public TextView getConfirmationDialogTitle() {
        return confirmationDialogTitle;
    }

    public TextView getConfirmationDialogText() {
        return confirmationDialogText;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_confirmation);
        

        setWidthAndHeight();

        setCanceledOnTouchOutside(false);

        confirmationDialogTitle = findViewById(R.id.confirmation_title);
        confirmationDialogText = findViewById(R.id.text_confirmation_dialog);

        acceptButton = findViewById(R.id.accept_button);
        cancelButton = findViewById(R.id.cancel_button);


    }

    private void setWidthAndHeight(){

        int width = (int)(context.getResources().getDisplayMetrics().widthPixels*0.60);
        this.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

}