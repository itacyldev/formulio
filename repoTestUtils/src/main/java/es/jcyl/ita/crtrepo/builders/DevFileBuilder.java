package es.jcyl.ita.crtrepo.builders;
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
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.test.utils.RandomUtils;
import es.jcyl.ita.formic.repo.media.FileEntity;
import es.jcyl.ita.formic.repo.media.FileRepository;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

/**
 * Class with helper methods to create and populate File repositories.
 */

public class DevFileBuilder {

    private File baseFolder;
    private int numEntities = 10;
    private FileRepository repository;
    private String[] names;

    public DevFileBuilder withBaseFolder(File baseFolder) {
        this.baseFolder = baseFolder;
        return this;
    }

    public DevFileBuilder withNumEntities(int num) {
        this.numEntities = num;
        return this;
    }

    public DevFileBuilder withNames(String[] names) {
        this.names = names;
        return this;
    }

    public void build() {
        if (this.baseFolder == null) {
            try {
                this.baseFolder = new File(FileUtils.getTempDirectory(), RandomUtils.randomString(10));
                if (!this.baseFolder.exists()) {
                    this.baseFolder.mkdir();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        this.repository = new FileRepository(baseFolder);
        if (names == null) {
            names = RandomUtils.randomObjectArray(numEntities, String.class);
        }
        File tmpFolder = new File(System.getProperty("java.io.tmpdir"));
        List<FileEntity> lstEntities = buildEntities(tmpFolder, names);
        for (FileEntity e : lstEntities) {
            repository.save(e);
        }
    }


    public FileRepository getRepository() {
        return this.repository;
    }

    ///////////////////////////////////////////////
    //// Helper functions
    ///////////////////////////////////////////////

    public static List<FileEntity> buildEntities(File baseFolder, String[] names) {
        List<FileEntity> lst = new ArrayList<FileEntity>(names.length);
        for (String name : names) {
            lst.add(buildEntity(baseFolder, name));
        }
        return lst;
    }

    public static FileEntity buildEntity() {
        File imgFile = RandomUtils.createRandomImageFile();
        FileEntity fe = FileEntity.fromFile(imgFile);
        return fe;
    }

    public static FileEntity buildEntity(File baseFolder, String name) {
        File imgFile = RandomUtils.createRandomImageFile(baseFolder, name);
        FileEntity fe = FileEntity.fromFile(imgFile);
        return fe;
    }

}
