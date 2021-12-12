package es.jcyl.ita.formic.jayjobs.task.reader;

import android.database.sqlite.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.greendao.database.StandardDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.RecordPage;
import es.jcyl.ita.formic.jayjobs.task.utils.TaskResourceAccessor;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.db.SQLQueryFilter;
import es.jcyl.ita.formic.repo.db.source.NativeSQLEntitySource;
import es.jcyl.ita.formic.repo.db.sqlite.RawSQLiteRepository;
import util.Log;

public class SQLReader extends AbstractReader {

    private String dbFile;
    private String sqlQuery;
    // TODO: parameter binding
//    private Map<String, Object> params;

    private Repository<Entity, SQLQueryFilter> repo;

    @Override
    public void open() throws TaskException {
        locateDbFile();
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        NativeSQLEntitySource source = new NativeSQLEntitySource(new StandardDatabase(db), sqlQuery);
        this.repo = new RawSQLiteRepository(source);
    }

    @Override
    public RecordPage read() throws TaskException {
        SQLQueryFilter filter = new SQLQueryFilter();
        filter.setPageSize(getPageSize());
        filter.setOffset(getOffset());
        List<Entity> entities = repo.find(filter);
        // convert to List<Map>
        RecordPage page = createRecordPage(entities);
        return page;
    }

    private RecordPage createRecordPage(List<Entity> entities) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Entity entity : entities) {
            rows.add(entity.getProperties());
        }
        RecordPage page = new RecordPage(rows);
        return page;
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

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    @Override
    public void close() {
        this.repo = null;
    }

    public String getDbFile() {
        return dbFile;
    }

    public void setDbFile(String dbFile) {
        this.dbFile = dbFile;
    }
}
