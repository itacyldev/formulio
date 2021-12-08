package es.jcyl.ita.formic.exporter.task.reader;


import es.jcyl.ita.formic.exporter.task.TaskSepItem;
import es.jcyl.ita.formic.exporter.task.models.RecordPage;

public interface Reader extends TaskSepItem {

    void open();

    RecordPage read();

    void setPageSize(Integer size);

    void setOffset(Integer offset);

    void setPaginate(Boolean active);

    Boolean allowsPagination();

    void close();
}
