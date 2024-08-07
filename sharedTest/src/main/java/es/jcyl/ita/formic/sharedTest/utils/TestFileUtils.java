package es.jcyl.ita.formic.sharedTest.utils;

/*
 *
 *  * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestFileUtils {

    private static final Log LOGGER = LogFactory.getLog(TestFileUtils.class);

    public static String getTmpFileName() {
        return getTmpFileName(null);
    }

    public static String getTmpFileName(String ext) {
        ext = (ext == null) ? "" : "." + ext;
        File file = new File(getBuildFolder(), RandomStringUtils.randomAlphanumeric(10) + ext);
        return file.getAbsolutePath().replace("\\", "/");
    }

    public static String getBuildFolder() {
        String rootFolder = System.getProperty("user.dir") + File.separator;
        return Paths.get(rootFolder, "target").toString().replace('\\', '/');
    }
    public static File getBuildFolderFile() {
        return new File(getBuildFolder());
    }
//
//    public static File getBuildFolder(){
//        ClassLoader classLoader = ContextTestUtils.class.getClassLoader();
//        URL resource = classLoader.getResource(".");
//        if (resource == null) {
//            throw new RuntimeException(String.format("Couldn't find build folder, make sure the file is included" +
//                    " in the test-resource folder, or in the device's sdcard."));
//        }
//        return new File(resource.getFile());
//    }

    public static String createFolder(String parent, String folder) throws IOException {
        Path path = Paths.get(parent, folder);
        Files.createDirectories(path);
        return path.toString();
    }

    public static String createTmpFolder()  {
        File tempDirectory = new File(getBuildFolder());
        String randomFolderName = RandomStringUtils.randomAlphanumeric(6);
        Path path = Paths.get(tempDirectory.toString(), randomFolderName);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return path.toString();
    }

    public static String createTmpFile(String content) {
        File tempDirectory = FileUtils.getTempDirectory();
        return createTmpFile(tempDirectory, content);
    }

    public static String createTmpFile() {
        File tempDirectory = new File(getBuildFolder());
        return createTmpFile(tempDirectory, RandomStringUtils.randomAlphabetic(200));
    }

    public static String createTmpFile(File folder) {
        return createTmpFile(folder, RandomStringUtils.randomAlphanumeric(200));
    }

    public static String createTmpFile(File folder, String content) {
        String outputFile = Paths
                .get(folder.getAbsolutePath(), RandomStringUtils.randomAlphanumeric(10) + ".html")
                .toString();
        // create test file
        try {
            FileUtils.write(new File(outputFile), content);
        } catch (IOException e) {
            throw new RuntimeException("Error al crear documento de test", e);
        }
        return outputFile;
    }

    public static boolean fileExists(String path) {
        File f = new File(path);
        return f.exists();
    }

    public static File findFile(String fileName) {
        ClassLoader classLoader = TestFileUtils.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new RuntimeException("No se ha encontrado el fichero: " + fileName);
        } else {
            File file = new File(resource.getFile());
            return file;
        }
    }

}
