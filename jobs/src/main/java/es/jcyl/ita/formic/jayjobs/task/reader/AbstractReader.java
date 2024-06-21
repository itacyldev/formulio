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

import es.jcyl.ita.formic.jayjobs.task.models.AbstractTaskSepItem;

/**
 * Base implementation for readers.
 *
 * @author: gustavo.rio@itacyl.es
 */

public abstract class AbstractReader extends AbstractTaskSepItem
		implements Reader {

	private Integer pageSize = 500;
	private Integer offset = 0;
	private Boolean paginate = false;

	@Override
	public void setPageSize(Integer size) {
		this.pageSize = size;
	}

	@Override
	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	@Override
	public void setPaginate(Boolean active) {
		this.paginate = active;
	}

	public Boolean getPaginate() {
		return this.paginate;
	}

	public Integer getOffset() {
		return this.offset;
	}

	public Integer getPageSize() {
		return this.pageSize;
	}

}
