package es.jcyl.ita.formic.jayjobs.task.executor;
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
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.IterativeTask;
import es.jcyl.ita.formic.jayjobs.task.models.RecordPage;
import es.jcyl.ita.formic.jayjobs.task.processor.Processor;
import es.jcyl.ita.formic.jayjobs.task.reader.Reader;
import es.jcyl.ita.formic.jayjobs.task.writer.DummyWriter;
import es.jcyl.ita.formic.jayjobs.task.writer.Writer;

/**
 * Class that handles the execution of iterative tasks.
 * An iterative task contains reader -> processors (optional) -> writer (optional)
 * The reader provides paginated results, each batch is passed to processor and writer before the next page is read.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */


public class IterativeTaskHandler implements TaskHandler<IterativeTask> {

    @Override
    public void handle(CompositeContext context, IterativeTask task)
            throws TaskException {

        int numPage = 0;
        configureTaskProcessingObjects(task);

        Reader reader = task.getReader();
        List<Processor> processors = task.getProcessors();
        Writer writer = task.getWriter();

        // initialize object
        reader.open();
        if (!reader.allowsPagination()) {
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
        } else {
            // iterar sobre los resultados para escritura paginada
            int offset = 0;
            int pagesize = task.getPageSize();
            reader.setOffset(offset);
            reader.setPageSize(pagesize);

            // initialize objects
            writer.open();

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
                // actualizar filtrado paginacion
                offset += task.getPageSize();
                reader.setOffset(offset);
                page = reader.read();
                numPage++;
            }
        }
        reader.close();
        if (writer != null) {
            writer.close();
        }
    }

    private void configureTaskProcessingObjects(IterativeTask task) throws TaskException {
        int pSize = task.getPageSize();
        boolean paginationActive = task.getPaginate();

        Reader reader = task.getReader();
        if (reader == null) {
            throw new TaskException(String.format("Iterative tasks must have a reader instance. " +
                    "Check configuration for task [%s].", task.getName()));
        } else {
            reader.setTask(task);
            reader.setPageSize(pSize);
            reader.setPaginate(paginationActive);
        }
        Writer writer = task.getWriter();
        if (writer == null) {
            // default writer to avoid nullpointer checks
            writer = new DummyWriter();
            task.setWriter(writer);
        }
        writer.setTask(task);
        writer.setPageSize(pSize);
        writer.setPaginate(paginationActive);

        if (CollectionUtils.isNotEmpty(task.getProcessors())) {
            for (Processor processor : task.getProcessors()) {
                processor.setTask(task);
            }
        }
    }

}
