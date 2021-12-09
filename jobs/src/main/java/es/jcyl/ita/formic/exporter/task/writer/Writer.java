package es.jcyl.ita.formic.exporter.task.writer;


import es.jcyl.ita.formic.exporter.task.models.RecordPage;
import es.jcyl.ita.formic.exporter.task.models.TaskSepItem;

public interface Writer extends TaskSepItem {

	public void open();

	public void write(RecordPage page);

	public void setPageSize(Integer size);

	public void setOffset(Integer offset);

	public void setPaginate(Boolean active);

	public void close();
}
