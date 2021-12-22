package es.jcyl.ita.formic.jayjobs.utils;
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

import android.database.sqlite.SQLiteDatabase;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;

import es.jcyl.ita.formic.repo.builders.DevDbBuilder;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class DbTestUtils {
    /**
     * Creates tests database with a randomly named table and inserts the number of rows
     *
     * @return: [dbFileName, tableName]
     */
    public static String[] createPopulatedDatabase(int numRows) {
        File tempDirectory = TestUtils.createTempDirectory();
        String dbFileName = String.format("%s/%s.sqlite", tempDirectory.getAbsolutePath(),
                RandomStringUtils.randomAlphabetic(10));
        String randomTableName = RandomStringUtils.randomAlphabetic(10);
        return createPopulatedDatabase(dbFileName, randomTableName, numRows);
    }

    public static String[] createPopulatedDatabase(String dbFileName, int numRows) {
        String randomTableName = RandomStringUtils.randomAlphanumeric(10);
        return createPopulatedDatabase(dbFileName, randomTableName, numRows);
    }

    public static String[] createPopulatedDatabase(String dbFileName, String tableName, int numRows) {
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbFileName, null);
        DevDbBuilder devDbBuilder = new DevDbBuilder();
        EntityMeta meta = DevDbBuilder.buildRandomMeta(tableName);
        devDbBuilder.withMeta(meta).withNumEntities(numRows).build(sqLiteDatabase);
        return new String[]{dbFileName, tableName};
    }
}
