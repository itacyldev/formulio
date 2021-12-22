package es.jcyl.ita.formic.jayjobs.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

import es.jcyl.ita.crtcyl.sync.exception.SyncException;
import es.jcyl.ita.crtcyl.util.ZipUtils;

/**
 * Created by ita-ramvalja on 19/04/2018.
 */

public class SyncUtils {

    protected static final Log LOGGER = LogFactory
            .getLog(SyncUtils.class);

    private static final int NUM_ZIPPING_RETRYS = 5;


    /**
     * @param origen
     * @param destino
     * @return
     */
    public static String zipFile(Context ctx, String origen, String destino)
            throws SyncException {
        String path = null;
        File zippedFile;
        int reintentos = 0;
        boolean fileZipped = false;
        try {
            while (reintentos < NUM_ZIPPING_RETRYS && !fileZipped) {

                FileOutputStream os = ctx.openFileOutput(destino, ctx
                        .MODE_PRIVATE);

                ZipUtils.zip(origen, os);

                zippedFile = ctx.getFileStreamPath(destino);
                if (zippedFile.exists()) {
                    fileZipped = true;
                    path = zippedFile.getAbsolutePath();
                }

                reintentos++;
            }

        } catch (Exception ex) {
            String message = "Error al comprimir el Fichero " + destino;
            LOGGER.error(message, ex);
            throw new SyncException(message);
        }
        return path;
    }


    public static void showAlert(Context ctx, String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Accept",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }


    /**
     * Get the las zipfile in the temp folder
     *
     * @param tmpFolder
     * @return
     */
    public static File getLastZipFile(File tmpFolder) {
        File lastFile = null;

        //obtenemos la lista de ficheros .zip del directorio
        File[] files = tmpFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase().endsWith(".zip");
            }
        });

        if (files.length == 1) {
            lastFile = files[0];
        } else if (files.length > 1) {
            lastFile = files[0];
            long last = -1;
            for (int i = 1; i < files.length; i++) {
                File currentFile = files[i];
                if (currentFile.lastModified() > lastFile.lastModified()) {
                    lastFile = currentFile;
                }
            }
        }
        return lastFile;
    }

}
