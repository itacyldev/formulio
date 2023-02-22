package es.jcyl.ita.formic.forms.project;/*
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

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.util.FileUtils;
import es.jcyl.ita.formic.jayjobs.task.utils.ContextAccessor;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public class ProjectImporter {

    private static ProjectImporter _instance;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectImporter.class);

    private static final Pattern projectNamePattern = Pattern.compile("^([a-zA-Z0-9]*)(-\\d*)?$");
    private static final DateFormat timeStamper = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static int BUFFER = 512;

    private static final String DIR_FORMS = "forms";
    private static final String DIR_DATA = "data";
    private static final String DIR_BAK = "bak";
    private static final String FORM_FILE_EXT = ".xml";
    private static final String DATA_FILE_EXT = ".sqlite";

    public enum Extensions {
        XML("xml"), SQLITE("sqlite"), JS("js");

        String extension;

        Extensions(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }
    }

    public static ProjectImporter getInstance() {
        if (_instance == null) {
            _instance = new ProjectImporter();
        }
        return _instance;
    }

    public Map<String, String> getExistingFiles(String pathStr, String destination) throws IOException {

        ZipFile zipFile = new ZipFile(pathStr);

        Map<String, String> existingFiles = new HashMap<>();

        ZipEntry ze = null;
        Enumeration entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ze = (ZipEntry) entries.nextElement();
            String name = ze.getName();
            if (ze.isDirectory()) {
                name = name.substring(0, name.lastIndexOf(File
                        .separator));
            } else {
                String newFileName = destination + File.separator +
                        name;
                String[] nameSplit = name.split(File.separator);
                if (nameSplit.length > 0) {
                    name = nameSplit[nameSplit.length - 1];
                }

                if (FileUtils.fileExists(newFileName) && EnumUtils.isValidEnum(Extensions.class, FileUtils.getFileExtension(new File(newFileName)).toUpperCase())) {
                    existingFiles.put(name, newFileName);
                }
            }
        }
        zipFile.close();
        return existingFiles;
    }

    public void extractFiles(Context context, String path, String
            destination) throws IOException {
        ZipFile zipFile = new ZipFile(path);

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
            } else {
                if (!FileUtils.fileExists(destination + File.separator + ze.getName())) {
                    new File(destination + File.separator + ze.getName()).mkdirs();
                }
            }
        }
        LOGGER.info("Ficheros extraidos");

    }

    public String getProjectName(Context context, String lastPathSegment) {
        String projectName = lastPathSegment.split(".frmd")[0];
        Matcher matcher = projectNamePattern.matcher(projectName);
        if (matcher.find()) {
            projectName = matcher.group(1);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString("projectName", projectName).apply();

        return projectName;
    }

    public String getPathString(Context context, Uri path) {
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


    public void moveFiles2BackUp(Context context, String projectName) {
        String dest = ContextAccessor.workingFolder(App.getInstance().getGlobalContext());
        new File(dest).mkdirs();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String projectsFolder = sharedPreferences.getString("current_workspace", Environment.getExternalStorageDirectory().getAbsolutePath() + "/projects");

        zipFolder(new File(projectsFolder), projectName, projectName, new File(dest), null);

    }

    /**
     * Zips a Folder to "[Folder].zip"
     *
     * @param toZipFolder Folder to be zipped
     * @return the resulting ZipFile
     */
    public File zipFolder(File toZipFolder, String zipName, String projectName, File dest, File subfolder) {
        File ZipFile = new File(dest != null ? dest : toZipFolder, String.format("%s_%s.%s", zipName, timeStamper.format(new Date()), "frmd"));
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(ZipFile));
            zipSubFolder(out, new File(toZipFolder.getPath() + File.separator + projectName), toZipFolder.getPath().length(), subfolder);
            out.close();
            return ZipFile;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Main zip Function
     *
     * @param out            Target ZipStream
     * @param folder         Folder to be zipped
     * @param basePathLength Length of original Folder Path (for recursion)
     */
    private void zipSubFolder(ZipOutputStream out, File folder, int basePathLength, File subfolder) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isDirectory()) {
                    if (subfolder == null || (subfolder!=null && file.getAbsolutePath().startsWith(subfolder.getAbsolutePath()))) {
                        zipSubFolder(out, file, basePathLength, subfolder);
                    }
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
}
