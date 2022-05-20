package es.jcyl.ita.formic.forms.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


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

    /**
     * Devuelve la extensi�n de un fichero.
     *
     * @param file
     * @return
     */
    public static String getFileExtension(final File file) {
        String output = null;

        if (file == null) {
            return output;
        }

        final int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            output = file.getName().substring(i + 1);
        }

        return output;
    }

    /**
     * Comprueba si existe un fichero.
     *
     * @param url
     * @return
     */
    public static boolean fileExists(final String url) {
        boolean output = true;

        if (url == null) {
            return output;
        }


        final File file = new File(url);
        if (!file.exists()) {
            output = false;
        }
        return output;
    }

    /**
     * Copia un archivo de origen en otro de destino.
     *
     * @param context
     * @param source
     * @param target
     * @throws IOException
     */
    public static void copyFile(Context context, File source, File target) throws IOException {
        if (source == null || target == null) {
            return;
        }

        if (!target.exists()) {
            target.createNewFile();
        }

        FileChannel sourceChannel = null;
        FileChannel targetChannel = null;

        try {
            sourceChannel = new FileInputStream(source).getChannel();
            targetChannel = new FileOutputStream(target).getChannel();
            targetChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

            refreshFilesystem(context, target);
        } finally {
            if (sourceChannel != null) {
                sourceChannel.close();
            }
            if (targetChannel != null) {
                targetChannel.close();
            }
        }
    }

    /**
     * Refresca un archivo en el sistema de archivos.
     *
     * @param context
     * @param file
     */
    public static void refreshFilesystem(final Context context, final File file) {
        if (context != null && file != null) {
            Uri contentUri;
            if (Build.VERSION.SDK_INT >= 24) {
                contentUri = FileProvider.getUriForFile(context,
                        context.getPackageName() + "" +
                                ".provider", file);
            } else {
                contentUri = Uri.fromFile(file);
            }


            final Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
            context.sendBroadcast(mediaScanIntent);
        }
    }

    /**
     * Copy a directory and all its contents recursively
     * @param context
     * @param origin
     * @param destination
     */
    public static void copyDirectory(Context context, String origin, String destination) throws IOException {
        checkCreateDirectory(destination);
        File directory = new File(origin);
        File f;
        if (directory.isDirectory()) {
            checkCreateDirectory(destination);
            String [] files = directory.list();
            if (files.length > 0) {
                for (String archivo : files) {
                    f = new File (origin + File.separator + archivo);
                    if(f.isDirectory()) {
                        checkCreateDirectory(destination+File.separator+archivo+File.separator);
                        copyDirectory(context, origin+File.separator+archivo+File.separator, destination+File.separator+archivo+File.separator);
                    } else { //Es un archivo
                        copyFile(context, new File(origin+File.separator+archivo), new File(destination+File.separator+archivo));
                    }
                }
            }
        }
    }

    /**
     * Check if a directory exists, and if not, create all the necessary path for it to exist
     * @param path
     */
    public static void checkCreateDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }



}
