package es.jcyl.ita.formic.exporter.task.models;

import java.util.List;
import java.util.Map;


public class RecordPage {

	PaginationInfo info;
	List<Map<String, Object>> results;

	public RecordPage(List<Map<String, Object>> resultSet) {
		this.results = resultSet;
		PaginationInfo pInfo = new PaginationInfo(resultSet);
//		pInfo.setFirst(resultSet.first());
//		pInfo.setPageSize(resultSet.pageSize());
//		pInfo.setTotalResult(resultSet.totalResult());
//		pInfo.setCurrentPageSize(resultSet.currentPageSize());
		
		this.info = pInfo;
	}

	public RecordPage(List<Map<String, Object>> results, PaginationInfo pInfo) {
		this.results = results;
		this.info = pInfo;
	}

	public PaginationInfo getInfo() {
		return this.info;
	}

	public void setInfo(PaginationInfo info) {
		this.info = info;
	}

	public List<Map<String, Object>> getResults() {
		return this.results;
	}

	public void setResults(List<Map<String, Object>> results) {
		this.results = results;
	}

}
