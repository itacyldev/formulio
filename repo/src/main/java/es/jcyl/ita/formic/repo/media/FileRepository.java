package es.jcyl.ita.formic.repo.media;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.repo.AbstractEditableRepository;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.RepositoryException;
import es.jcyl.ita.formic.repo.media.meta.FileMeta;
import es.jcyl.ita.formic.repo.media.query.FileEntityExpression;
import es.jcyl.ita.formic.repo.media.source.FileEntitySource;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;
import es.jcyl.ita.formic.repo.query.BaseFilter;
import es.jcyl.ita.formic.repo.source.EntitySource;

/**
 * Repository implementation to handle files through repository api.
 * The repository uses a folder as base root to insert/find the file entities. The EntityId will be
 * the file path relative to the base folder.
 * <p>
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FileRepository extends AbstractEditableRepository<FileEntity, String, BaseFilter<FileEntityExpression>> {

    private static FileMeta META = new FileMeta();
    private final File baseFolder;
    private String defaultExtension;

    public FileRepository(File baseFolder) {
        this.baseFolder = baseFolder;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    protected void doSave(FileEntity entity) {
        if (this.hasKey(entity)) {
            this.update(entity);
        } else {
            this.insert(entity);
        }
    }

    @Override
    protected FileEntity doFindById(String key) {
        File f = new File(baseFolder, key);
        if (!f.exists()) {
            return null;
        }
        FileEntity entity = newEntity();
        readEntityData(f, entity);
        return entity;
    }

    @Override
    public List<FileEntity> doFind(BaseFilter<FileEntityExpression> filter) {
        File[] files;
        if (filter == null) {
            files = baseFolder.listFiles();
        } else {
            files = baseFolder.listFiles(filter.getExpression());
        }
        List<FileEntity> lstEntities = new ArrayList<FileEntity>(files.length);
        FileEntity entity;
        for (File f : files) {
            entity = newEntity();
            readEntityData(f, entity);
            lstEntities.add(entity);
        }
        return lstEntities;
    }

    @Override
    public long count(BaseFilter<FileEntityExpression> filter) {
        return find(filter).size();
    }

    @Override
    protected List<FileEntity> doListAll() {
        return find(null);
    }

    @Override
    public EntitySource getSource() {
        return null;
    }

    @Override
    public EntityMeta getMeta() {
        return META;
    }

    @Override
    public Object getImplementor() {
        return null;
    }

    @Override
    public Class getFilterClass() {
        return null;
    }


    /**
     * Inserts the file content in the repository folder and fills the entity id and calculated attributes
     *
     * @param entity
     */
    private void insert(FileEntity entity) {
        String baseName, ext;
        String fileName = (String) entity.get("name");

        // if no fileName given create one
        if (StringUtils.isBlank(fileName)) {
            baseName = RandomStringUtils.randomAlphanumeric(20);
            ext = defaultExtension;
        } else {
            baseName = FilenameUtils.getBaseName(fileName);
            ext = FilenameUtils.getExtension(fileName);
        }
        fileName = baseName + "." + ext;
        File output = new File(baseFolder, fileName);

        // if it already exists append random suffix to create new file
        if (output.exists()) {
            baseName += "_" + RandomStringUtils.randomAlphanumeric(5);
            fileName = baseName + "." + ext;
            output = new File(baseFolder, fileName);
        }
        // assign entity id
        readEntityData(output, entity);
        String id = (String) entity.getId();
        ByteArray content = (ByteArray) entity.get("content");
        saveEntityContent(id, content);
    }

    /**
     * Modifies the content of the given entity
     *
     * @param entity
     */
    private void update(FileEntity entity) {
        File f = saveEntityContent((String) entity.getId(), (ByteArray) entity.get("content"));
        readEntityData(f, entity);
    }

    private File saveEntityContent(String id, ByteArray content) {
        File output = new File(this.baseFolder, id);
        try {
            FileUtils.writeByteArrayToFile(output, content.getValue());
        } catch (Exception e) {
            throw new RepositoryException("An error occurred while trying to write " +
                    "file content to location: " + output.getAbsolutePath());
        }
        return output;
    }

    private boolean hasKey(FileEntity entity) {
        return entity.getId() != null;
    }

    /**
     * The id of the entity will be the file path relative to base folder.
     *
     * @param f: entity file
     * @return
     */
    protected String createEntityKey(File f) {
        String str = f.getAbsolutePath().replace(baseFolder.getAbsolutePath(), "");
        if (str.startsWith(File.separator)) {
            str = str.substring(1); // removes first "/"
        }
        return str;
    }


    @Override
    public void doDelete(FileEntity entity) {
        deleteById((String) entity.getId());
    }

    @Override
    public void doDeleteById(String id) {
        File f = new File(this.baseFolder, id);
        f.delete();
    }

    @Override
    public void doDeleteAll() {
        try {
            FileUtils.deleteDirectory(baseFolder);
        } catch (IOException e) {
            throw new RepositoryException("Can't delete folder: " + baseFolder);
        }
        baseFolder.mkdir();
    }

    @Override
    public FileEntity newEntity() {
        FileEntitySource source = new FileEntitySource(baseFolder);
        FileEntity entity = new FileEntity(source, META);
        return entity;
    }

    private void readEntityData(File f, FileEntity entity) {
        String id = createEntityKey(f);
        entity.set("id", id);
        entity.set("name", f.getName());
        entity.set("parentPath", baseFolder.getAbsolutePath());
        entity.set("absolutePath", f.getAbsolutePath());
        entity.set("size", f.length());
        entity.set("file", f);
    }


    public String getDefaultExtension() {
        return defaultExtension;
    }

    public void setDefaultExtension(String defaultExtension) {
        this.defaultExtension = defaultExtension;
    }
}
