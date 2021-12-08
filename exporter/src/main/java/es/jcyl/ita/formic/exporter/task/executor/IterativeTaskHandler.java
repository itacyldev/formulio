package es.jcyl.ita.formic.exporter.task.executor;
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

import org.mini2Dx.collections.CollectionUtils;

import java.util.List;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.exporter.task.exception.TaskException;
import es.jcyl.ita.formic.exporter.task.listener.LogFileTaskListener;
import es.jcyl.ita.formic.exporter.task.models.IterativeTask;
import es.jcyl.ita.formic.exporter.task.models.RecordPage;
import es.jcyl.ita.formic.exporter.task.models.Task;
import es.jcyl.ita.formic.exporter.task.models.TaskListener;
import es.jcyl.ita.formic.exporter.task.processor.Processor;
import es.jcyl.ita.formic.exporter.task.reader.Reader;
import es.jcyl.ita.formic.exporter.task.writer.DummyWriter;
import es.jcyl.ita.formic.exporter.task.writer.Writer;

/**
 * Class that handles the execution of iterative tasks.
 *
 * @author gustavo.rio@itacyl.es
 */


public class IterativeTaskHandler implements TaskHandler<IterativeTask> {

    @Override
    public void handle(CompositeContext context, IterativeTask task, TaskExec taskExecutionInfo)
            throws TaskException {

        int numPage = 0;
        try {
            configureTaskProcessingObjects(task);

            Reader reader = task.getReader();
            List<Processor> processors = task.getProcessors();
            Writer writer = task.getWriter();
            if (writer == null) {
                // establecemos una implementaci�n vac�a para evitar
                // nullpointers
                writer = new DummyWriter();
            }

            task.getListener().init();
            // initialize object
            reader.open();
            if (!reader.allowsPagination()) {
                task.getListener().beforePage(numPage);
                RecordPage page = reader.read();
                if (CollectionUtils.isNotEmpty(processors)) {
                    for (Processor proc : processors) {
                        page = proc.process(page);
                    }
                }
                if (writer != null) {
                    writer.open();
                    writer.write(page);
                }
                task.getListener().afterPage(numPage);
            } else {
                // iterar sobre los resultados para escritura paginada
                int offset = 0;
                int pagesize = task.getPageSize();
                reader.setOffset(offset);
                reader.setPageSize(pagesize);

                // initialize objects
                writer.open();

                task.getListener().beforePage(numPage);
                RecordPage page = reader.read();
                while (page != null && CollectionUtils.isNotEmpty(page.getResults())) {
                    if (CollectionUtils.isNotEmpty(processors)) {
                        for (Processor proc : processors) {
                            page = proc.process(page);
                        }
                    }
                    if (writer != null) {
                        writer.setOffset(offset);
                        writer.write(page);
                    }
                    task.getListener().afterPage(numPage);
                    // actualizar filtrado paginacion
                    offset += task.getPageSize();
                    reader.setOffset(offset);
                    task.getListener().beforePage(numPage);
                    page = reader.read();
                    numPage++;
                }
            }
            reader.close();
            if (writer != null) {
                writer.close();
            }

            task.getListener().end();
        } catch (Throwable e) {
            task.getListener().errorOnPage(numPage);
            throw e;
        }
    }

    private void configureTaskProcessingObjects(IterativeTask task) {
        int pSize = task.getPageSize();
        boolean paginationActive = task.getPaginate();

        Reader reader = task.getReader();
        if (reader != null) {
            reader.setTask(task);
            reader.setPageSize(pSize);
            reader.setPaginate(paginationActive);
        }
        Writer writer = task.getWriter();
        if (writer != null) {
            writer.setTask(task);
            writer.setPageSize(pSize);
            writer.setPaginate(paginationActive);
        }
        if (CollectionUtils.isNotEmpty(task.getProcessors())) {
            for (Processor prss : task.getProcessors()) {
                prss.setTask(task);
            }
        }
        configureListener(task);
    }

    private void configureListener(Task task) {
        if (task.getListener() == null) {
            // default task listener
            TaskListener tlistener = new LogFileTaskListener();
            task.setListener(tlistener);
            tlistener.setTask(task);
        }
    }

}
