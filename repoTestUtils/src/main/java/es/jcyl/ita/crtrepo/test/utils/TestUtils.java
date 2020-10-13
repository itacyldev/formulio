package es.jcyl.ita.crtrepo.test.utils;
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
import java.io.IOException;
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
    public static String readSource(final File file) throws IOException {


        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        return toString(reader);
    }

    protected static String toString(BufferedReader reader) throws IOException {
        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append('\n');
        }
        return buffer.toString();
    }
}
