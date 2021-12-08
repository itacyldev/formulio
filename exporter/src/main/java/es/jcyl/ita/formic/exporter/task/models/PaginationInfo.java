package es.jcyl.ita.formic.exporter.task.models;

import java.io.Serializable;
import java.util.List;

public class PaginationInfo implements Serializable {
	Integer first;
	Integer pageSize;
	Integer totalResult;
	Integer currentPageSize;

	public PaginationInfo(List collection) {
		totalResult = collection.size();
		currentPageSize = collection.size();
		first = 0;
		pageSize = totalResult;
	}

	public int currentPage() {
		if (first >= totalResult() || first < 0) {
			// pedimos un datos fuera de rango la p�gina devuelta es -1
			return -1;
		} else {
			return first / pageSize;
		}
	}

	public int currentPageSize() {
		return currentPageSize;
	}
	public int pageSize() {
		return pageSize;
	}

	public int first() {
		return first;
	}

	public int totalResult() {
		return totalResult;
	}

	public Integer getFirst() {
		return first;
	}

	public void setFirst(Integer first) {
		this.first = first;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getTotalResult() {
		return totalResult;
	}

	public void setTotalResult(Integer totalResult) {
		this.totalResult = totalResult;
	}

	public Integer getCurrentPageSize() {
		return currentPageSize;
	}

	public void setCurrentPageSize(Integer currentPageSize) {
		this.currentPageSize = currentPageSize;
	}

}
