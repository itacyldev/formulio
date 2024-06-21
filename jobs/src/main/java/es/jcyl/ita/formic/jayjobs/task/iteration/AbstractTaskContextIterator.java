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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.MultiMapCompositeContext;
import es.jcyl.ita.formic.jayjobs.task.models.GroupTask;

public abstract class AbstractTaskContextIterator implements TaskContextIterator {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractTaskContextIterator.class);

    protected int idx = 0;
    protected GroupTask task;

    private CompositeContext globalContext;
    private Context groupIterContext;
    private CompositeContext currentIterCtx;

    /**
     * inner state vars
     */
    private boolean initialized = false;

    protected final void init() {
        // create the GroupTask context for groupTask and add to the global context so it
        // can be accessed from outside of the GroupTask
        groupIterContext = new BasicContext(task.getName());
        groupIterContext.put("size", task.getIterSize());
        globalContext.addContext(groupIterContext);
        doInit();
        initialized = true;
    }

    abstract void doInit();

    @Override
    public final synchronized boolean hasNext() {
        if (!initialized) {
            init();
        }
        LOGGER.info(String.format("Starting iteration [%s] in group task [%s]", idx, this.task.getName()));
        // prepare iteration context
        // update index in the group context
        groupIterContext.put("idx", idx);


        // create context for current iteration and add global context
        currentIterCtx = new MultiMapCompositeContext();
        currentIterCtx.addContext(globalContext);
        idx++;

        return doHasNext();
    }

    protected abstract boolean doHasNext();

    @Override
    public final synchronized CompositeContext next() {
        return currentIterCtx;
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

    protected CompositeContext getCurrentIterCtx() {
        return currentIterCtx;
    }

    public Context getGroupIterContext() {
        return groupIterContext;
    }

    @Override
    public void remove() {
        // do nothing
    }
}
