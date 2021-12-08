package es.jcyl.ita.formic.repo.test.utils;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class TestUtils {

    public static File findFile(String fileName) {
        ClassLoader classLoader = TestUtils.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new RuntimeException(String.format("Coudn't find file %s, make sure the file is included" +
                    " in the test-resource folder, or in the device's sdcard.", fileName));
        }
        File file = new File(resource.getFile());
        return file;
    }

    public static String readAsString(final String file) {
        File f = TestUtils.findFile(file);
        return readAsString(f);
    }

    public static String readAsString(final File file) {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            return toString(reader);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while reading the file " + file, e);
        }
    }

    protected static String toString(BufferedReader reader) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append('\n');
        }
        return buffer.toString();
    }

    /**
     * Get a resource using classpath and copy it as a tmp file to return it. It's meant to be
     * used in Instrumented tests, where tests resources are bundled in apk an cannot be accessed
     * directly as File objects.
     *
     * @param resource
     * @return
     */
    public static File getResourceAsFile(String resource) {
        try {
            InputStream in = TestUtils.class.getClassLoader().getResourceAsStream(resource);
            if (in == null) {
                throw new IllegalArgumentException(String.format("Resource not found [%s]", resource));
            }

            File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                //copy stream
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
