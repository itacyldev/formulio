package es.jcyl.ita.formic.app.projectimport;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;

import androidx.annotation.RequiresApi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.forms.util.FileUtils;

/**
 * Created by ita-ramvalja on 02/02/2018.
 */

public class ImporterUtils {
    private static int BUFFER = 512;
    protected static final Log LOGGER = LogFactory.getLog(ImporterUtils.class);

    private static int READ_LIMIT = 1024 * 1024 * 100;

    private static final String DIR_FORMS = "forms";
    private static final String DIR_DATA = "data";
    private static final String DIR_BAK = "bak";
    private static final String FORM_FILE_EXT = ".xml";
    private static final String DATA_FILE_EXT = ".sqlite";

    private static final Pattern projectNamePattern = Pattern.compile("^([a-zA-Z0-9]*)(-\\d*)?$");

    // Comprueba la estructura carpetas y si contiene fichero de
    // conf de proyecto

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void importProjectFile(final Uri path, final String
            destination, Context context) {
        importProjectFile(path, destination, context,
                false, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void importProjectFile(final Uri path, final String
            destination, Context context, final boolean checkFileStructure, final boolean backUpFiles){
        importProjectFile(path, destination, context, checkFileStructure, backUpFiles, true);
    }

    /**
     * @param path               Ruta del fichero de configuraciOn que vamos a
     *                           importar
     * @param destination        Ruta donde se descomprimirA el fichero
     * @param context
     * @param checkFileStructure Determina si hay que comprobar o no si la
     *                           estructura del fichero de configuration es
     *                           correcta
     * @param backUpFiles        Determina si hacemos copia de seguridad de
     *                           los ficheros
     * @param showExistingFilesDialog Indica si es necesario mostrar el dialogo de ficheros existentes.
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void importProjectFile(final Uri path, final String
            destination, Context context, final boolean checkFileStructure, final boolean backUpFiles, final boolean showExistingFilesDialog) {

        boolean isFormsDir = false;
        boolean isDataDir = false;
       Map<String, String> existingFiles = new HashMap<>();

        try {
            String projectName = getProjectName(path.getLastPathSegment());
            Matcher matcher = projectNamePattern.matcher(projectName);
            if (matcher.find()) {
                projectName = matcher.group(1);
            }

            String dataFile = DIR_DATA + File.separator + projectName +
                    DATA_FILE_EXT;


            String pathStr = getPathString(context, path);

            if ("".equals(pathStr)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context, es.jcyl.ita.formic.forms.R.style.DialogStyle);
                final AlertDialog dialog = builder.setMessage(R.string.projectimportfail)
                        .setPositiveButton(R.string.accept,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            final DialogInterface cancelDialog,
                                            final int id) {
                                        cancelDialog.cancel();
                                        launchActivity(context, "");
                                    }
                                }).create();

                dialog.show();
            } else {

                ZipFile zipFile = new ZipFile(pathStr);

                ZipEntry ze = null;
                Enumeration entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ze = (ZipEntry) entries.nextElement();
                    String name = ze.getName();
                    if (ze.isDirectory()) {
                        name = name.substring(0, name.lastIndexOf(File
                                .separator));
                        if (name.equalsIgnoreCase(DIR_FORMS)) {
                            isFormsDir = true;
                        }

                        if (name.equalsIgnoreCase(DIR_DATA)) {
                            isDataDir = true;
                        }

                    } else {
                       String newFileName = destination + File.separator +
                                name;
                        String[] nameSplit = name.split(File.separator);
                        if (nameSplit.length > 0) {
                            name = nameSplit[nameSplit.length - 1];
                        }

                        if (FileUtils.fileExists(newFileName)) {
                            existingFiles.put(name, newFileName);
                        }
                    }

                }


                if (checkFileStructure & (!isFormsDir || !isDataDir)) {
                    String message = context.getString(R.string.project_import_file_incorrect) +
                            "- " + DIR_FORMS + "\n- " + DIR_DATA + "\n" + context.getString(R.string
                            .project_import_files) +
                            "- " + dataFile;

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context, es.jcyl.ita.formic.forms.R.style.DialogStyle);
                    final AlertDialog dialog = builder.setMessage(message)
                            .setPositiveButton(R.string.accept,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                final DialogInterface cancelDialog,
                                                final int id) {
                                            cancelDialog.cancel();
                                        }
                                    }).create();

                    dialog.show();
                } else {


                    if (existingFiles.size() > 0 && showExistingFilesDialog) {
                        showExistingFilesDialog(context, existingFiles, pathStr,
                                destination, projectName, backUpFiles);
                    } else {
                        if (backUpFiles) {
                            moveFiles2BackUp(context, destination, projectName, new ArrayList<>(existingFiles
                                    .values()));
                        }
                        extractFiles(context, pathStr, destination, projectName);
                    }

                }

                zipFile.close();
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("No existe el fichero " + path.getPath(), e);
        } catch (IOException e) {
            LOGGER.error(e);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private static String getPathString(Context context, Uri path) {
        String pathStr = FileUtils.getPath(context, path);
        if (!FileUtils.fileExists(pathStr)) {
            pathStr = pathStr.replace("/root", "");
            if (!FileUtils.fileExists(pathStr)) {
                if (pathStr.startsWith("/downloads")) {
                    pathStr = pathStr.replace("downloads", "storage/emulated/0" +
                            "/Download");
                    if (!FileUtils.fileExists(pathStr)) {
                        pathStr = pathStr.replace("storage/emulated/0", "sdcard");
                        if (!FileUtils.fileExists(pathStr)) {
                            pathStr = "";
                        }
                    }
                }
            }
        }
        return pathStr;
    }

    private static String getProjectName(String lastPathSegment) {
        String projectName = lastPathSegment.split(".frmd")[0];
        // Si el fichero se ha descargado mAs de una vez al nombre se le
        // anade " (1)", " (2)", etc... y hay que eliminarlo
        //projectName = projectName.split(" ")[0];
        return projectName;
    }

    private static void showExistingFilesDialog(final Context context, final
    Map<String, String> files, final String path, final String destination,
                                                final String projectName,
                                                final boolean backupFiles) {

        String message = context.getString(R.string.project_import_existing_files);

        for (String file : files.keySet()) {
            message += "\n- " + file;
        }

        message += "\n" + context.getString(R.string
                .project_import_overwrite_files);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, es.jcyl.ita.formic.forms.R.style.DialogStyle);
        final AlertDialog dialog = builder.setMessage(message)
                .setPositiveButton(R.string.accept,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    final DialogInterface dialog,
                                    final int id) {
                                if (backupFiles) {
                                    moveFiles2BackUp(context, destination, projectName, new ArrayList<>(files
                                            .values()));
                                }
                                extractFiles(context, path, destination, projectName);
                                dialog.dismiss();
                            }
                        }).setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                //launchActivity(context, projectName);
                                dialog.dismiss();
                            }
                        }).create();
        dialog.show();

    }

    private static void extractFiles(Context context, String path, String
            destination, String projectName) {
        try {
            ZipFile zipFile = new ZipFile(path);

            /*if (!FileUtils.fileExists(destination + File.separator + projectName)){
                new File(destination + File.separator + projectName).mkdirs();
            }*/

            ZipEntry ze = null;

            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ze = (ZipEntry) entries.nextElement();
                if (!ze.isDirectory()) {
                    String newFileName = destination + File.separator +
                            ze.getName();

                    new File(newFileName).getParentFile().mkdirs();

                    LOGGER.info("Extrayendo fichero " + newFileName);

                    FileOutputStream fout = new FileOutputStream
                            (newFileName);
                    BufferedOutputStream bufout = new BufferedOutputStream(fout);
                    byte[] buffer = new byte[BUFFER];

                    int read1;

                    InputStream inputStream = zipFile.getInputStream(ze);


                    while ((read1 = inputStream.read(buffer)) != -1) {
                        bufout.write(buffer, 0, read1);
                    }

                    inputStream.close();
                    bufout.close();
                    fout.close();
                }else{
                    if (!FileUtils.fileExists(destination + File.separator + ze.getName())){
                        new File(destination + File.separator + ze.getName()).mkdirs();
                    }
                }
            }
            LOGGER.info("Ficheros extraidos");

            launchActivity(context, projectName);

        } catch (Exception e) {
            LOGGER.error("Error al extraer los ficheros", e);
        }
    }

    private static void launchActivity(Context context, String projectName) {
        setSharedPreferences(context, projectName);

        /*App app = App.getInstance();
        ProjectRepository projectRepo = app.getProjectRepo();
        Project prj = projectRepo.findById(projectName);
        app.setCurrentProject(prj);
        ((MainActivity) context).loadFragment(ProjectListFragment.newInstance(
                App.getInstance().getProjectRepo()));*/


       final Intent intent =
                context.getPackageManager()
                        .getLaunchIntentForPackage(
                                context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    private static void moveFiles2BackUp(Context context, String destination, String projectName, List<String> files) {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String dest = sharedPreferences.getString("current_workspace", Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + DIR_BAK + File.separator + projectName);
            String origin = destination + File.separator + projectName;
            FileUtils.copyDirectory(context, origin, dest);
        } catch (IOException e) {
            LOGGER.error("Error al copiar el proyecto " + projectName + " a la " +
                    "carpeta temporal", e);
        }
    }

    private static void setSharedPreferences(Context context, String
            selectedFileName) {
        final SharedPreferences.Editor editor = context
                .getSharedPreferences("selectedProject", Context.MODE_PRIVATE)
                .edit();
        editor.putString("name", selectedFileName);
        editor.commit();
    }

    /**
     * Zips a Folder to "[Folder].zip"
     * @param toZipFolder Folder to be zipped
     * @return the resulting ZipFile
     */
    public static File zipFolder(File toZipFolder, String projectName) {
        File ZipFile = new File(toZipFolder, String.format("%s.frmd", projectName));
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(ZipFile));
            zipSubFolder(out,  new File(toZipFolder.getPath() + File.separator + projectName), toZipFolder.getPath().length());
            out.close();
            return ZipFile;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Main zip Function
     * @param out Target ZipStream
     * @param folder Folder to be zipped
     * @param basePathLength Length of original Folder Path (for recursion)
     */
    private static void zipSubFolder(ZipOutputStream out, File folder, int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];

                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath.substring(basePathLength + 1);

                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(file.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);

                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
                out.closeEntry();
            }
        }
    }

}
