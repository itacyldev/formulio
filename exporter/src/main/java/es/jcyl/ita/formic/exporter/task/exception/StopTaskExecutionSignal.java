package es.jcyl.ita.formic.exporter.task.exception;
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
 * Special exception used to make the executor stop the execution of a job.
 *
 * @author gustavo.rio@itacyl.es
 */

public class StopTaskExecutionSignal extends RuntimeException {

	public StopTaskExecutionSignal(String msg, Throwable t) {
		super(msg, t);
	}

	public StopTaskExecutionSignal(Throwable t) {
		super(t);
	}

	public StopTaskExecutionSignal(String msg) {
		super(msg);
	}
}
