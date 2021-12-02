package es.jcyl.ita.formic.forms.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;


public class FileUtils {


    /**
     * Obtenemos el path a partir de una URI.
     *
     * @param context
     * @param uri
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getPath(final Context context, final Uri uri) {
        String output = null;

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            final String[] projection
                    = new String[] {MediaStore.Images.ImageColumns.DATA};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,
                        null, null, null);
                final int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);

                if (cursor.moveToFirst()) {
                    output = cursor.getString(column_index);
                }

                if (output == null) {
                    output = getPathWithDocumentsContract(context, uri);
                }
            } catch (final Exception e) {
                // Si getContentResolver falla se prueba con DocumentsContract
                output = getPathWithDocumentsContract(context, uri);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return output;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getPathWithDocumentsContract(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        final boolean isNougat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;

        // DocumentProvider
        if("com.android.externalstorage.documents".equals(uri.getAuthority())
        || "com.android.providers.downloads.documents".equals(uri.getAuthority())
        || "com.android.providers.media.documents".equals(uri.getAuthority())){
            String docId = null;
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)){
                docId = DocumentsContract.getDocumentId(uri);
            }else if (isNougat && DocumentsContract.isTreeUri(uri)){
                docId = DocumentsContract.getTreeDocumentId(uri);
            }
            if (docId == null){
                return null;
            }
            final String[] split = docId.split(":");
            final String type = split[0];

            if ("primary".equalsIgnoreCase(type)) {
                String external_storage_path = Environment.getExternalStorageDirectory() + "/";
                if (split.length > 1) {
                    return external_storage_path + split[1];
                } else {
                    return external_storage_path;
                }
            } else if ("raw".equalsIgnoreCase(type)){
                return split[1];
            } else if ("image".equalsIgnoreCase(type)){
                String[] column = { MediaStore.Images.Media.DATA };
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = context.getContentResolver().
                        query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                column, sel, new String[]{ split[1] }, null);
                String filePath = "";
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
                return filePath;
            }else if ("video".equalsIgnoreCase(type)){
                //todo
            }else if ("audio".equalsIgnoreCase(type)){
                //todo
            }else {
                return "storage" + "/" + docId.replace(":", "/");
            }
        }
        return null;
    }

}
