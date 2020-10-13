package es.jcyl.ita.formic.repo.query;
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

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public abstract class AbstractFilter<E extends Expression> implements Filter<E> {
    private int pageSize = -1;
    private int offset;
    private E expression;
    private Sort[] sorting;

    // TODO: very basic implementation to prototype
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public Sort[] getSorting() {
        return sorting;
    }

    public void setSorting(Sort[] sorting) {
        this.sorting = sorting;
    }

    public void setExpression(E expression) {
        this.expression = expression;
    }

    public E getExpression() {
        return this.expression;
    }

}
