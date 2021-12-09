package es.jcyl.ita.formic.exporter.task.reader;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import es.jcyl.ita.formic.exporter.task.models.PaginatedList;
import es.jcyl.ita.formic.exporter.task.models.PaginationInfo;
import es.jcyl.ita.formic.exporter.task.models.RecordPage;


/**
 * Devuelve una p�gina con datos aleatorios cada vez que se llama al m�tod read
 * hasta que se alcanza el l�mite de maxResults.
 * <p>
 * Pensado para casos de test.
 *
 * @author gustavo.rio@itacyl.es
 */
public class RandomDataReader extends AbstractReader {

    private int numColumns = 5;
    private int maxResults = 100;
    private int _currentTolal = 0;

    @Override
    public void open() {
    }

    @Override
    public RecordPage read() {
        if (this._currentTolal >= this.maxResults) {
            return null;
        }
        RecordPage record = createRandomPage(this.getPageSize(),
                this.numColumns);
        this._currentTolal += record.getResults().size();
        PaginationInfo pInfo = new PaginationInfo(
                record.getResults());
        pInfo.setTotalResult(this._currentTolal);
        record.setInfo(pInfo);
        return record;
    }

    @Override
    public Boolean allowsPagination() {
        return true;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        this._currentTolal = 0;
    }

    public int getNumColumns() {
        return this.numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    private RecordPage createRandomPage(int numItems, int numColumns) {
        PaginatedList<Map<String, Object>> lstSrc = new PaginatedList<Map<String, Object>>();

        List<String> columns = new ArrayList<String>();
        for (int i = 0; i < numColumns; i++) {
            columns.add("c_" + (i + 1));
        }
        Map<String, Object> record;

        for (int i = 0; i < numItems; i++) {
            record = new HashMap<>();
            for (int j = 0; j < columns.size(); j++) {
                switch (j % 3) {
                    case 0: // string
                        record.put(columns.get(j),
                                RandomStringUtils.randomAlphanumeric(10));
                        break;
                    case 1: // big Long
                        long min = 10000000;
                        long max = Long.MAX_VALUE;
                        long value = ThreadLocalRandom.current().nextLong(min, max);
                        // record.put(columns.get(j), value);
                        record.put(columns.get(j), new BigDecimal(40));

                        break;
                    case 2: // double
                        double minimum = 1000000;
                        double random = new Random().nextDouble();
                        Double dValue = Double.valueOf(
                                minimum + random * (Integer.MAX_VALUE - minimum));
                        record.put(columns.get(j), dValue);
                        break;
                }
            }
            lstSrc.add(record);
        }
        addRandomItems(lstSrc, numItems, numColumns, false);

        int pageSize = 3;
        lstSrc.setFirst(0);
        lstSrc.setPageSize(pageSize);
        lstSrc.setTotalResult(lstSrc.size());

        RecordPage page = new RecordPage(lstSrc);
        return page;
    }

    public static List<Map<String, Object>> addRandomItems(
            List<Map<String, Object>> lstSrc, int numItems, int numColumns,
            boolean conSufijo) {

        List<String> columns = new ArrayList<String>(numColumns);
        for (int c = 1; c <= numColumns; c++) {
            columns.add("col" + c
                    + (conSufijo ? "_" + RandomStringUtils.randomAlphanumeric(5)
                    : ""));
        }

        return addRandomItems(lstSrc, numItems, columns, true);
    }

    public static List<Map<String, Object>> addRandomItems(
            List<Map<String, Object>> lstSrc, int numItems,
            List<String> columns, boolean conSufijo) {
        // Map<String, Object> record;
        Map<String, Object> record;

        for (int i = 0; i < numItems; i++) {
            record = new HashMap<>();
            for (int j = 0; j < columns.size(); j++) {
                record.put(columns.get(j),
                        RandomStringUtils.randomAlphanumeric(10));
            }
            lstSrc.add(record);
        }
        return lstSrc;
    }
}
