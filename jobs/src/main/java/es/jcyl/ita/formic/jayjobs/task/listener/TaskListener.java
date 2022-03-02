package es.jcyl.ita.formic.jayjobs.task.listener;

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
 * author:
 */

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.jayjobs.task.models.Task;

public interface TaskListener {
    void setTask(Task t);

    void init();

    void beforePage(int page);

    void afterPage(int page);

    void beforeStep(int pos);

    void afterStep(int pos);

    void error(int pos, String data1, String data2);

    void error(int pos, CompositeContext context);

    /**
     * Notifica error mientras se procesaba la p�gina indicada.
     *
     * @param page
     */
    void errorOnPage(int page);

    void end();

}
