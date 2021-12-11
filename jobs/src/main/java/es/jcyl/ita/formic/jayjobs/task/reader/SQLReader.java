package es.jcyl.ita.formic.jayjobs.task.reader;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.PaginatedList;
import es.jcyl.ita.formic.jayjobs.task.models.RecordPage;
import es.jcyl.ita.formic.jayjobs.task.reader.sql.SQLiteQueryEngine;
import es.jcyl.ita.formic.jayjobs.task.utils.TaskResourceAccessor;
import util.Log;

public class SQLReader extends AbstractReader {
    private static final int DEFAULT_CACHE_LIMIT = 256;

    private String dbFile;
    private String querySQL;
    private Map<String, Object> params;
    private Boolean nameBinding = false;
    private Boolean doCount = true;

    /**
     * Variables de estado interno
     */
    // cache de consultas parseadas
    private final Map<String, ParsedSQLWrapper> parsedSqlCache = new LinkedHashMap<String, ParsedSQLWrapper>(
            DEFAULT_CACHE_LIMIT, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Entry<String, ParsedSQLWrapper> eldest) {
            return size() > DEFAULT_CACHE_LIMIT;
        }
    };

    private SQLiteQueryEngine qEngine = new SQLiteQueryEngine();
    private QueryProcessorEH qProcessor;

    public SQLReader() {
        qProcessor = QueryProcessorEH.getInstance();
    }

    @Override
    public void open() throws TaskException {
        locateDbFile();
    }

    @Override
    public RecordPage read() throws TaskException{

        String pQuery = qProcessor.process(querySQL, getGlobalContext());
        Log.debug("pQuery =" + pQuery);

        if (!doCount) {
            setPageSize(-1);
        }

        PaginatedList rs = qEngine.execute(pQuery, dbFile, getGlobalContext(), getPageSize(),
                getOffset());

        RecordPage rp = new RecordPage(rs);

        return rp;
    }

    private void locateDbFile() throws TaskException {
        if (StringUtils.isBlank(dbFile)) {
            throw new TaskException("You must set the 'dbFile' attribute in sqlReader to define the target db file.");

        }
        this.dbFile = TaskResourceAccessor.getWorkingFile(this.getGlobalContext(), dbFile);
        Log.info("SqlReader configured to access data in dbFile: " + this.dbFile);
    }

    @Override
    public Boolean allowsPagination() {
        return true;
    }

    public String getQuerySQL() {
        return querySQL;
    }

    public void setQuerySQL(String querySQL) {
        this.querySQL = querySQL;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public SQLiteQueryEngine getqEngine() {
        return qEngine;
    }

    public void setqEngine(SQLiteQueryEngine qEngine) {
        this.qEngine = qEngine;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
    }

    public Boolean getNameBinding() {
        return nameBinding;
    }

    public void setNameBinding(Boolean nameBinding) {
        this.nameBinding = nameBinding;
    }


    public Boolean isDoCount() {
        return doCount;
    }

    public void setDoCount(Boolean doCount) {
        this.doCount = doCount;
    }
}
