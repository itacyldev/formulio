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

import es.jcyl.ita.formic.jayjobs.task.models.AbstractTaskSepItem;

public abstract class AbstractWriter extends AbstractTaskSepItem implements Writer {

	private Integer pageSize;
	private Integer offset;
	private Boolean paginate;

	@Override
	public void setPageSize(Integer size) {
		pageSize = size;
	}

	@Override
	public void setOffset(Integer size) {
		offset = size;
	}

	@Override
	public void setPaginate(Boolean active) {
		paginate = active;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Boolean getPaginate() {
		return paginate;
	}

	public Integer getOffset() {
		return offset;
	}

}
