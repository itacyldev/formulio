package es.jcyl.ita.formic.exporter.task.models;
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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class AbstractTaskSepItem implements TaskSepItem {
	private String type;
	private String className;

	@JsonIgnore
	protected Task task;

	public Task getTask() {
		return task;
	}

	@Override
	public void setTask(Task task) {
		this.task = task;
	}

	protected Context getTaskContext() {
		return getTask().getTaskContext();
	}

	protected CompositeContext getGlobalContext() {
		return (getTask() == null) ? null : getTask().getGlobalContext();
	}

	protected TaskListener getListener() {
		return (getTask() == null) ? null : getTask().getListener();
	}

	protected void error(int pos, String data1, String data2) {
		getListener().error(pos, data1, data2);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
