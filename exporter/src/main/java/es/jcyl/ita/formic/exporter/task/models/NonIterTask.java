package es.jcyl.ita.formic.exporter.task.models;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import es.jcyl.ita.formic.exporter.task.processor.NonIterProcessor;

@JsonIgnoreProperties({ "processor" })
public class NonIterTask extends AbstractTask {

	@JsonIgnore
	private List<NonIterProcessor> processors;

	public NonIterTask() {
	}

	public NonIterTask(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public List<NonIterProcessor> getProcessors() {
		return this.processors;
	}

	public void setProcessors(List<NonIterProcessor> processors) {
		this.processors = processors;
	}

	public void setProcessor(NonIterProcessor processor) {
		if (processor == null) {
			this.processors = Collections.EMPTY_LIST;
		} else {
			this.processors = Collections.singletonList(processor);
		}
	}

}
