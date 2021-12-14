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
import es.jcyl.ita.formic.jayjobs.task.models.GroupTask;

public class BasicCounterIterator extends AbstractTaskContextIterator {

    public BasicCounterIterator() {

    }

    public BasicCounterIterator(CompositeContext globalContext, GroupTask task) {
        setTask(task);
        setGlobalContext(globalContext);
    }

    @Override
    void doInit() {
    }

    @Override
    protected boolean doHasNext() {
        return idx < task.getIterSize();
    }

    @Override
    protected Context createTaskContext() {
        Context ctx = new BasicContext(task.getName());
        ctx.put("idx", idx);
        return ctx;
    }

    @Override
    protected Context createGroupContext(GroupTask t) {
        Context ctx = new BasicContext(t.getName());
        ctx.put("size", t.getIterSize());
        return ctx;
    }

}
