package es.jcyl.ita.formic.jayjobs.task.models;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.jayjobs.task.processor.Processor;
import es.jcyl.ita.formic.jayjobs.task.reader.Reader;
import es.jcyl.ita.formic.jayjobs.task.writer.Writer;

@JsonIgnoreProperties({"processor"})
public class IterativeTask extends AbstractTask {

    private String query;
    private Boolean isTableData;
    /* Data source con el que se quiere ejecutar la tarea */
    @JsonIgnore
    private Reader reader;
    @JsonIgnore
    private Writer writer;
    @JsonIgnore
    private List<Processor> processors;

    /**
     * Pagination info
     */
    private Integer pageSize = 200;
    private Boolean paginate = true;

    public IterativeTask() {
    }

    public IterativeTask(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Indica si los resultados de la consula deben ser tratados como una tabla
     * o como variables individuales
     *
     * @return
     */
    public Boolean isTableData() {
        return this.isTableData;
    }

    public void setIsTableData(Boolean isTableData) {
        this.isTableData = isTableData;
    }

    private Map<String, Object> params;

    public Map<String, Object> getParams() {
        return this.params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Reader getReader() {
        return this.reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Writer getWriter() {
        return this.writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public List<Processor> getProcessors() {
        return this.processors;
    }

    public void setProcessors(List<Processor> processors) {
        this.processors = processors;
    }

    public void setProcessor(Processor processor) {
        if (processor == null) {
            this.processors = Collections.EMPTY_LIST;
        } else {
            this.processors = Arrays.asList(processor);
        }
    }

    public Boolean getIsTableData() {
        return this.isTableData;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Boolean getPaginate() {
        return this.paginate;
    }

    public void setPaginate(Boolean paginate) {
        this.paginate = paginate;
    }

}
