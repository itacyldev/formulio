package es.jcyl.ita.formic.jayjobs.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by ita-ramvalja on 19/04/2018.
 */

public class SyncUtils {

    protected static final Logger LOGGER = LoggerFactory.getLogger(SyncUtils.class);

    private static final int NUM_ZIPPING_RETRYS = 5;


    /**
     * @param origen
     * @param destino
     * @return
     */
    public static String zipFile( String origen, String destino)
             {
                 return null;
//        String path = null;
//        File zippedFile;
//        int reintentos = 0;
//        boolean fileZipped = false;
//        try {
//            while (reintentos < NUM_ZIPPING_RETRYS && !fileZipped) {
//
//                FileOutputStream os = ctx.openFileOutput(destino, ctx
//                        .MODE_PRIVATE);
//
//                //ZipUtils.zip(origen, os);
//
//                zippedFile = ctx.getFileStreamPath(destino);
//                if (zippedFile.exists()) {
//                    fileZipped = true;
//                    path = zippedFile.getAbsolutePath();
//                }
//
//                reintentos++;
//            }
//
//        } catch (Exception ex) {
//            String message = "Error al comprimir el Fichero " + destino;
//            LOGGER.error(message, ex);
//            throw new RuntimeException(message);
//        }
//        return path;
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
