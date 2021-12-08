package es.jcyl.ita.formic.exporter.task.processor;

import es.jcyl.ita.formic.exporter.task.TaskSepItem;
import es.jcyl.ita.formic.exporter.task.models.RecordPage;

public interface Processor extends TaskSepItem {

    RecordPage process(RecordPage page);

}
