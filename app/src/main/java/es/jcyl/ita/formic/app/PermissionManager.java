package es.jcyl.ita.formic.app;
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

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import es.jcyl.ita.formic.R;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class PermissionManager {
    private static final int PERMISSION_REQUEST = 1234;
    private static final int RQS_OPEN_DOCUMENT_TREE = 2;
    private static final int PERMISSION_STORAGE_REQUEST = 5708463;
    private static final int PROJECT_IMPORT_FILE_SELECT = 725353137;

    private final Activity activity;

    public PermissionManager(Activity activity) {
        this.activity = activity;
    }

    public boolean askStoragePermissionNeed() {
        int result = ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(activity, CAMERA);
        return !(result == PackageManager.PERMISSION_GRANTED ||
                result1 == PackageManager.PERMISSION_GRANTED ||
                result2 == PackageManager.PERMISSION_GRANTED);
    }


    private void warnStoragePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, es.jcyl.ita.formic.forms.R.style.DialogStyle);
        builder.setMessage(R.string.specificDirectoryAccessPermission).setPositiveButton(R.string.close, new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                requestStoragePermission();
            }
        });

        builder.create().show();
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            activity.startActivityForResult(intent, RQS_OPEN_DOCUMENT_TREE);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{READ_EXTERNAL_STORAGE,
                            WRITE_EXTERNAL_STORAGE, CAMERA},
                    RQS_OPEN_DOCUMENT_TREE);
        }
    }
//   https://developer.android.com/training/location/permissions#request-location-access-runtime
//    public void requestLocationPermission(){
//        ActivityResultLauncher<String[]> locationPermissionRequest =
//                activity.registerForActivityResult(new ActivityResultContracts
//                                .RequestMultiplePermissions(), result -> {
//                            Boolean fineLocationGranted = result.ge(
//                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
//                            Boolean coarseLocationGranted = result.getOrDefault(
//                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
//                            if (fineLocationGranted != null && fineLocationGranted) {
//                                // Precise location access granted.
//                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
//                                // Only approximate location access granted.
//                            } else {
//                                // No location access granted.
//                            }
//                        }
//                );
//    }
}
