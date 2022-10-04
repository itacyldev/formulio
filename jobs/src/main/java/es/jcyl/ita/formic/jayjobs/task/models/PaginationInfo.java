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

import java.io.Serializable;
import java.util.List;

/**
 * Bean that holds resultSet pagination info
 *
 * @author: Gustavo Río (gustavo.rio@itacyl.es)
 */

public class PaginationInfo implements Serializable {
    Integer first;
    Integer pageSize;
    Integer totalResult;
    Integer currentPageSize;

    public PaginationInfo(List collection) {
        totalResult = collection.size();
        currentPageSize = collection.size();
        first = 0;
        pageSize = totalResult;
    }

    public int currentPage() {
        if (first >= totalResult() || first < 0) {
            // pedimos un datos fuera de rango la p�gina devuelta es -1
            return -1;
        } else {
            return first / pageSize;
        }
    }

    public int currentPageSize() {
        return currentPageSize;
    }

    public int pageSize() {
        return pageSize;
    }

    public int first() {
        return first;
    }

    public int totalResult() {
        return totalResult;
    }

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalResult() {
        return totalResult;
    }

    public void setTotalResult(Integer totalResult) {
        this.totalResult = totalResult;
    }

    public Integer getCurrentPageSize() {
        return currentPageSize;
    }

    public void setCurrentPageSize(Integer currentPageSize) {
        this.currentPageSize = currentPageSize;
    }

}
