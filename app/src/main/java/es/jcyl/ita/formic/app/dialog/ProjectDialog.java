package es.jcyl.ita.formic.app.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.app.MainActivity;

/**
 * Created by ita-ramvalja on 30/05/2018.
 */

public class ProjectDialog {

    private final MainActivity mainActivity;
    protected String currentTheme;

    private static final int PROJECT_IMPORT_FILE_SELECT = 725353137;

    public ProjectDialog(final MainActivity mainActivity, String currentTheme) {
        this.mainActivity = mainActivity;
        this.currentTheme = currentTheme;
    }

    public AlertDialog build() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity, currentTheme.equals("light")?AlertDialog.THEME_HOLO_LIGHT:AlertDialog.THEME_HOLO_DARK);

        AlertDialog alertDialog = builder
                .setTitle(R.string.projects)
                .setItems(R.array.open_project_array,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    case 0:
                                        importProject();
                                        return;
                                    default:
                                        return;
                                }
                            }
                        }).create();
        alertDialog.show();
        alertDialogConfiguration(alertDialog);
        return alertDialog;
    }

    private AlertDialog alertDialogConfiguration(AlertDialog alertDialog){
        int dividerId = alertDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = alertDialog.findViewById(dividerId);
        int textViewId = alertDialog.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
        TextView tv = (TextView) alertDialog.findViewById(textViewId);
        int textColor, backgroundColor;
        if (currentTheme.equals("light")) {
            textColor = es.jcyl.ita.formic.forms.R.color.light_PrimaryColor;
        } else {
            textColor = es.jcyl.ita.formic.forms.R.color.dark_PrimaryColor;
        }
        divider.setBackgroundColor(mainActivity.getResources().getColor(textColor));
        tv.setTextColor(mainActivity.getResources().getColor(textColor));
        return alertDialog;
    }

    protected final void importProject() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        final String mime_type = MimeTypeMap.getSingleton().hasExtension("frmd")?
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("frmd") :
                "*/*";
        intent.setType(mime_type);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {

            mainActivity.startActivityForResult(
                    Intent.createChooser(intent,
                            mainActivity.getString(R.string.file_to_load)),
                    PROJECT_IMPORT_FILE_SELECT);

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mainActivity,
                    mainActivity.getString(R.string.error_filemanager),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
