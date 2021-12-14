package es.jcyl.ita.formic.jayjobs.task.iteration;
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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.MultiMapCompositeContext;
import es.jcyl.ita.formic.jayjobs.task.models.GroupTask;

public abstract class AbstractTaskContextIterator implements TaskContextIterator {

	protected int idx = 0;
	protected GroupTask task;

	protected CompositeContext globalContext;
	protected BasicContext sharedContext;
	protected Context groupIterContext;

	/** vars privadas gesti�n del flujo */
	private boolean initialized = false;

	protected final void init() {
		groupIterContext = createGroupContext(task);
		globalContext.addContext(groupIterContext);
		sharedContext = new BasicContext(task.getName() + ".shared");
		globalContext.addContext(sharedContext);
		doInit();
		initialized = true;
	}

	protected Context createGroupContext(GroupTask t) {
		Context ctx = new BasicContext(t.getName());
		ctx.put("size", t.getIterSize());
		return ctx;
	}

	abstract void doInit();

	@Override
	public final synchronized boolean hasNext() {
		if (!initialized) {
			init();
		}
		return doHasNext();
	}

	protected abstract boolean doHasNext();

	@Override
	public final synchronized CompositeContext next() {
		// update index in shared context
		groupIterContext.put("idx", idx);

		// create context for current task
		CompositeContext currentInterCtx = new MultiMapCompositeContext();

		// add shared contexts
		currentInterCtx.addContext(globalContext);
		currentInterCtx.addContext(sharedContext);
		currentInterCtx.addContext(groupIterContext);

		// add current task context
		currentInterCtx.addContext(createTaskContext());
		idx++;
		return currentInterCtx;
	}

	protected abstract Context createTaskContext();

	public GroupTask getTask() {
		return task;
	}

	@Override
	public void setTask(GroupTask task) {
		this.task = task;
	}

	public CompositeContext getGlobalContext() {
		return globalContext;
	}

	@Override
	public void setGlobalContext(CompositeContext globalContext) {
		this.globalContext = globalContext;
	}

	@Override
	public void remove() {
		// do nothing
	}
}
