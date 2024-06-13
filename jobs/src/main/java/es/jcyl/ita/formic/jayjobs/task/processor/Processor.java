package es.jcyl.ita.formic.jayjobs.task.processor;

import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.RecordPage;
import es.jcyl.ita.formic.jayjobs.task.models.TaskSepItem;

public interface Processor extends TaskSepItem {

    RecordPage process(RecordPage page) throws TaskException;

}
