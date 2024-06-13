package es.jcyl.ita.formic.jayjobs.task.writer;/*
 * Copyright 2020 Gustavo Río (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
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

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.jayjobs.task.models.RecordPage;

/**
 * Writer component to store items from reader in the global job context
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class ContextWriter extends AbstractWriter {
    public static String RESULT_LIST_KEY = "list";
    public static String PAGINATION_INFO = "info";

    private boolean rowAsVariables = false;
    private boolean placeHoldersInserted = false;

    @Override
    public void open() {
    }

    @Override
    public void write(RecordPage page) {
        if (rowAsVariables && !placeHoldersInserted) {
            // insert just the first line of the result set
            insertRowAsVars(page);
            placeHoldersInserted = true;
        } else {
            if (getTaskContext().containsKey(RESULT_LIST_KEY)) {
                List<Map<String, Object>> records = (List<Map<String, Object>>) getTaskContext()
                        .get(RESULT_LIST_KEY);
                records.addAll(page.getResults());
            } else {
                getTaskContext().put(RESULT_LIST_KEY, page.getResults());
            }
            getTaskContext().put(PAGINATION_INFO, page.getInfo());
        }
    }

    private void insertRowAsVars(RecordPage page) {
        List<Map<String, Object>> results = page.getResults();
        if (CollectionUtils.isNotEmpty(results)) {
            Map<String, Object> row = results.get(0);
            for (String key : row.keySet()) {
                // insertar variables en el contexto de la tarea
                getTaskContext().put(key, row.get(key));
            }
        }
    }

    @Override
    public void close() {
        if (!rowAsVariables && !getTaskContext().containsKey(RESULT_LIST_KEY)) {
            getTaskContext().put(RESULT_LIST_KEY, new ArrayList<Map<String, Object>>());
        }
    }

    public boolean isRowAsVariables() {
        return rowAsVariables;
    }

    public void setRowAsVariables(boolean rowAsVariables) {
        this.rowAsVariables = rowAsVariables;
    }

}
