package es.jcyl.ita.formic.jayjobs.task.writer;

import android.database.sqlite.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.greendao.database.StandardDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import es.jcyl.ita.formic.jayjobs.task.config.TaskConfigException;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.RecordPage;
import es.jcyl.ita.formic.jayjobs.task.reader.sql.SQLiteQueryEngine;
import es.jcyl.ita.formic.jayjobs.task.utils.ContextAccessor;
import es.jcyl.ita.formic.jayjobs.task.utils.TaskResourceAccessor;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.db.SQLQueryFilter;
import es.jcyl.ita.formic.repo.db.source.NativeSQLEntitySource;
import es.jcyl.ita.formic.repo.db.sqlite.RawSQLiteRepository;

public class SQLWriter extends AbstractWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLWriter.class);

    /**
     * Variables configurables a trav�s de JSON
     */
    private String dbFile;
    private String sqlQuery;

     private Repository<Entity, SQLQueryFilter> repo;

    private SQLiteQueryEngine qEngine = new SQLiteQueryEngine();

    @Override
    public void open() throws TaskException {
        locateDbFile();
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        NativeSQLEntitySource source = new NativeSQLEntitySource(new StandardDatabase(db), sqlQuery);
        this.repo = new RawSQLiteRepository(source);
    }

    @Override
    public void write(RecordPage page) throws TaskException {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        db.execSQL(sqlQuery);
        //qEngine.execute(sqlQuery, dbFile, getGlobalContext(), getPageSize(), getOffset());
    }

    @Override
    public void close() throws TaskException {
        this.repo = null;
    }

    /**
     * Tries to locate db file using relative reference to project folder and app working file folder
     *
     * @throws TaskException
     */
    private void locateDbFile() throws TaskException {
        if (StringUtils.isBlank(dbFile)) {
            throw new TaskException("You must set the 'dbFile' attribute in sqlWriter to define the target db file.");
        }
        this.dbFile = TaskResourceAccessor.getProjectFile(this.getGlobalContext(), dbFile);
        File f = new File(dbFile);
        if (!f.exists()) {
            this.dbFile = TaskResourceAccessor.getWorkingFile(this.getGlobalContext(), dbFile);
        }
        f = new File(dbFile);
        if (!f.exists()) {
            throw new TaskConfigException(String.format("Couldn't find file [%s] neither in project " +
                            "folder [%s] nor in application tmp folder [%s].", this.dbFile,
                    ContextAccessor.projectFolder(this.getGlobalContext()),
                    ContextAccessor.workingFolder(this.getGlobalContext())));
        }
        LOGGER.info("SqlWriter configured to access data in dbFile: " + this.dbFile);
    }

    public String getDbFile() {
        return dbFile;
    }

    public void setDbFile(String dbFile) {
        this.dbFile = dbFile;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }
}
