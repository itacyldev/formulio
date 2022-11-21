package es.jcyl.ita.formic.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import es.jcyl.ita.formic.R;

public class StartupActivity extends Activity implements DialogInterface.OnShowListener {

    AlertDialog alertDialog  = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showAlertDialog();


    }

    private void startActivity() {
        //This method can launch the mstr app after all your operations are done
        Intent intent = new Intent(this, MainActivity.class);
        Uri uriData = getIntent().getData();
        if(uriData != null) {
            intent.setData(uriData);
            intent.setAction(android.content.Intent.ACTION_VIEW);
        }
        startActivity(intent);
    }

    private void showAlertDialog(){
        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, es.jcyl.ita.formic.forms.R.style.DialogStyle);
        builder.setView(R.layout.layout_loading_dialog);
        alertDialog = builder.create();
        alertDialog.setOnShowListener(this);
        alertDialog.show();
    }

    @Override
    public void onShow(DialogInterface dialog) {
        startActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alertDialog.dismiss();
    }
}


