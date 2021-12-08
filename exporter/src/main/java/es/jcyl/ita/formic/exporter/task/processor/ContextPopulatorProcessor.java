package es.jcyl.ita.formic.exporter.task.processor;
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
 * Procesor that allows to set values in context
 *
 * @author gustavo.rio@itacyl.es
 *
 */

public class ContextPopulatorProcessor extends AbstractProcessor
		implements NonIterProcessor {

	/**
	 * Objeto de configuraci�n directamente mapeado desde la configuraci�n json
	 */
	private Object value;
	private String name = "value";

	@Override
	public void process() {
		this.getTaskContext().put(this.name, this.value);
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}