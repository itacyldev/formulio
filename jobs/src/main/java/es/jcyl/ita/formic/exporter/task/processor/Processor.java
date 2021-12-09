package es.jcyl.ita.formic.exporter.task.processor;

import es.jcyl.ita.formic.exporter.task.models.RecordPage;
import es.jcyl.ita.formic.exporter.task.models.TaskSepItem;

public interface Processor extends TaskSepItem {

    RecordPage process(RecordPage page);

}
