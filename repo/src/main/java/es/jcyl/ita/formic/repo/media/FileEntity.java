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

import java.io.File;
import java.io.IOException;

import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.media.meta.FileMeta;
import es.jcyl.ita.formic.repo.media.source.FileEntitySource;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FileEntity extends Entity<FileEntitySource, FileMeta> {

    private static FileMeta META = new FileMeta();

    public FileEntity(FileEntitySource source, FileMeta meta) {
        super(source, meta);
    }

    public FileEntity(FileEntitySource source, FileMeta meta, Object id) {
        super(source, meta, id);
    }

    @Override
    public Object get(String prop) {
        if (prop.equalsIgnoreCase("content")) {
            Object content = super.get(prop);
            if (content != null) {
                return content;
            } else {
                // lazy loading property
                File f = getFile();
                if (f != null) {
                    byte[] bytes = new byte[0];
                    try {
                        bytes = FileUtils.readFileToByteArray(f);
                    } catch (IOException e) {
                        throw new IllegalStateException("Cannot read content property, the file " +
                                "doesn't exists or is not accessible.", e);
                    }
                    return new ByteArray(bytes);
                }
            }
        }
        return super.get(prop);
    }

    public File getFile() {
        Object f = this.get("file");
        return (f == null) ? null : (File) f;
    }

    /**
     * Creates entity instance from file
     *
     * @param f
     * @return
     */
    public static FileEntity fromFile(File f) {
        FileEntitySource source = new FileEntitySource(f.getParentFile());
        FileEntity entity = new FileEntity(source, META);
        entity.set("name", f.getName());
        entity.set("size", f.length());
        try {
            byte[] bytes = FileUtils.readFileToByteArray(f.getAbsoluteFile());
            entity.set("content", new ByteArray(bytes));
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't read given file: " + f.getAbsolutePath());
        }
        return entity;
    }
}
