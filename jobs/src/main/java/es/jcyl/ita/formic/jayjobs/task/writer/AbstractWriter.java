package es.jcyl.ita.formic.jayjobs.task.writer;


import es.jcyl.ita.formic.jayjobs.task.models.AbstractTaskSepItem;

public abstract class AbstractWriter extends AbstractTaskSepItem implements Writer {

	private Integer pageSize;
	private Integer offset;
	private Boolean paginate;

	@Override
	public void setPageSize(Integer size) {
		pageSize = size;
	}

	@Override
	public void setOffset(Integer size) {
		offset = size;
	}

	@Override
	public void setPaginate(Boolean active) {
		paginate = active;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Boolean getPaginate() {
		return paginate;
	}

	public Integer getOffset() {
		return offset;
	}

}
