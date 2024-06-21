package es.jcyl.ita.formic.forms.jobs.reader;/*
 * Copyright 2020 Gustavo Río (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.builders.ComponentBuilderFactory;
import es.jcyl.ita.formic.forms.config.builders.repo.RepoConfigBuilder;
import es.jcyl.ita.formic.jayjobs.task.config.TaskConfigException;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.RecordPage;
import es.jcyl.ita.formic.jayjobs.task.reader.AbstractReader;
import es.jcyl.ita.formic.jayjobs.task.utils.ContextAccessor;
import es.jcyl.ita.formic.jayjobs.task.utils.TaskResourceAccessor;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.db.SQLQueryFilter;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public class RepoReader extends AbstractReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DevConsole.class);

    private String dbFile;
    private String dbTable;
    private String id;

    private Repository repo;

    @Override
    public void open() throws TaskException {
        //TaskConfigFactory factory = TaskConfigFactory.getInstance();
        //factory.addTaskStep("REPOREADER", this.getClass());

        locateDbFile();

        RepoConfigBuilder repoConfigBuilder = (RepoConfigBuilder) ComponentBuilderFactory.getInstance().getBuilder("repo");
        repo = repoConfigBuilder.createFromFile(id, dbFile, dbTable);
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

    @Override
    public void close() throws TaskException {
        this.repo = null;
    }

    @Override
    public Boolean allowsPagination() {
        return true;
    }

    private RecordPage createRecordPage(List<Entity> entities) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Entity entity : entities) {
            rows.add(entity.getProperties());
        }
        RecordPage page = new RecordPage(rows);
        return page;
    }

    /**
     * Tries to locate db file using relative reference to project folder and app working file folder
     *
     * @throws TaskException
     */
    private void locateDbFile() throws TaskException {
        if (StringUtils.isBlank(dbFile)) {
            throw new TaskException("You must set the 'dbFile' attribute in sqlReader to define the target db file.");
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
        LOGGER.info("SqlReader configured to access data in dbFile: " + this.dbFile);
    }

    public String getDbFile() {
        return dbFile;
    }

    public void setDbFile(String dbFile) {
        this.dbFile = dbFile;
    }

    public String getDbTable() {
        return dbTable;
    }

    public void setDbTable(String dbTable) {
        this.dbTable = dbTable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Repository getRepo() {
        return repo;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }
}
