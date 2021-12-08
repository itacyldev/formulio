package es.jcyl.ita.formic.exporter.task.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import es.jcyl.ita.formic.exporter.task.processor.Processor;
import es.jcyl.ita.formic.exporter.task.reader.Reader;
import es.jcyl.ita.formic.exporter.task.writer.Writer;

@JsonIgnoreProperties({ "processor" })
public class IterativeTask extends AbstractTask {

	private String query;
	private Boolean isTableData;
	/* Data source con el que se quiere ejecutar la tarea */
	@JsonIgnore
	private Reader reader;
	@JsonIgnore
	private Writer writer;
	@JsonIgnore
	private List<Processor> processors;

	/** Pagination info */
	private Integer pageSize = 200;
	private Boolean paginate = true;

	public IterativeTask() {
	}

	public IterativeTask(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Indica si los resultados de la consula deben ser tratados como una tabla
	 * o como variables individuales
	 * 
	 * @return
	 */
	public Boolean isTableData() {
		return this.isTableData;
	}

	public void setIsTableData(Boolean isTableData) {
		this.isTableData = isTableData;
	}

	private Map<String, Object> params;

	public Map<String, Object> getParams() {
		return this.params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public String getQuery() {
		return this.query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public Reader getReader() {
		return this.reader;
	}

	public void setReader(Reader reader) {
		this.reader = reader;
	}

	public Writer getWriter() {
		return this.writer;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}

	public List<Processor> getProcessors() {
		return this.processors;
	}

	public void setProcessors(List<Processor> processors) {
		this.processors = processors;
	}

	public void setProcessor(Processor processor) {
		if (processor == null) {
			this.processors = Collections.EMPTY_LIST;
		} else {
			this.processors = Arrays.asList(processor);
		}
	}

	public Boolean getIsTableData() {
		return this.isTableData;
	}

	public Integer getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Boolean getPaginate() {
		return this.paginate;
	}

	public void setPaginate(Boolean paginate) {
		this.paginate = paginate;
	}

}
