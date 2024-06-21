package es.jcyl.ita.formic.jayjobs.task.reader;

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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.PaginationInfo;
import es.jcyl.ita.formic.jayjobs.task.models.RecordPage;

/**
 * Simple class to provide items from an List through the reader interface
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class InMemoryArrayReader extends AbstractReader {

    /**
     * Name of the context property where the collection is store
     */
    private String source;

    private List<Map<String, Object>> resultset;

    public InMemoryArrayReader() {
    }

    public InMemoryArrayReader(List<Map<String, Object>> records) {
        this.resultset = records;
    }

    @Override
    public void open() {
    }

    /**
     * Paginates over the referenced list
     */
    @Override
    public RecordPage read() throws TaskException {

        List<Map<String, Object>> r = new ArrayList<Map<String, Object>>();
        if (StringUtils.isEmpty(this.source)) {
            for (int i = this.getOffset(); i < this.resultset.size()
                    && i < this.getOffset() + this.getPageSize(); i++) {
                r.add(this.resultset.get(i));
            }
        } else {
            Object obj = this.getGlobalContext().get(this.source);
            if (!(obj instanceof List)) {
                throw new TaskException(String.format("Cannot read the variable [%s] it is not a List instance",
                        this.source));
            }
            List list = (List) obj;

            Object ctxItem = null;
            for (int i = this.getOffset(); i < list.size()
                    && i < this.getOffset() + this.getPageSize(); i++) {
                ctxItem = list.get(i);
                Map<String, Object> mapItem = new LinkedHashMap<String, Object>();
                if (ctxItem instanceof Map) {
                    // copiar el mapa
                    mapItem.putAll((Map) ctxItem);
                } else {
                    mapItem.put("key", list.get(i));
                }
                r.add(mapItem);
            }
        }

        RecordPage result = new RecordPage(r, new PaginationInfo(r));
        return result;
    }

    @Override
    public void close() {
    }

    @Override
    public Boolean allowsPagination() {
        return true;
    }

    public List<Map<String, Object>> getResultset() {
        return this.resultset;
    }

    public void setResultset(List<Map<String, Object>> resultset) {
        this.resultset = resultset;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
