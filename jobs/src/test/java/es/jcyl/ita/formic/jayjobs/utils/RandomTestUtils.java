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

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class RandomTestUtils {

    public static List<Map<String, Object>> emptyResultSet() {
        return new ArrayList<Map<String, Object>>();
    }

    public static List<Map<String, Object>> addRandomItems(List<Map<String, Object>> lstSrc,
                                                           int numItems, int numColumns) {
        // create columns
        List<String> columns = new ArrayList<>(numColumns);
        for (int c = 0; c < numColumns; c++) {
            columns.add("col" + c + RandomStringUtils.randomAlphanumeric(5));
        }
        Map<String, Object> record;
        for (int i = 0; i < numItems; i++) {
            record = new HashMap<>();
            for (int j = 0; j < numColumns; j++) {
                record.put(columns.get(j),RandomStringUtils.randomAlphanumeric(5));
            }
            lstSrc.add(record);
        }
        return lstSrc;
    }


}
