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

import java.util.Iterator;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.task.models.GroupTask;

public interface TaskContextIterator extends Iterator<CompositeContext> {

    void setGlobalContext(CompositeContext ctx);

    void setTask(GroupTask t);

    /**
     * If returns true the next iteration is executed. The evaluation is make at the beginning of each iteration.
     */
    boolean evalEnterIterationExpr();

    /**
     * If returns true, the loops is stopped. The evaluation is made at the end of each iteration.
     */
    boolean evalExitIterationExpr();
}
