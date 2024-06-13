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

import java.util.List;
import java.util.Map;


public class RecordPage {

    PaginationInfo info;
    List<Map<String, Object>> results;

    public RecordPage(List<Map<String, Object>> resultSet) {
        this.results = resultSet;
        PaginationInfo pInfo = new PaginationInfo(resultSet);
//		pInfo.setFirst(resultSet.first());
//		pInfo.setPageSize(resultSet.pageSize());
//		pInfo.setTotalResult(resultSet.totalResult());
//		pInfo.setCurrentPageSize(resultSet.currentPageSize());

        this.info = pInfo;
    }

    public RecordPage(List<Map<String, Object>> results, PaginationInfo pInfo) {
        this.results = results;
        this.info = pInfo;
    }

    public PaginationInfo getInfo() {
        return this.info;
    }

    public void setInfo(PaginationInfo info) {
        this.info = info;
    }

    public List<Map<String, Object>> getResults() {
        return this.results;
    }

    public void setResults(List<Map<String, Object>> results) {
        this.results = results;
    }

}
