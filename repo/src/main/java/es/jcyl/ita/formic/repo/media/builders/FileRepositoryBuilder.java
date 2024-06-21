package es.jcyl.ita.formic.repo.media.builders;
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

import java.io.File;

import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.builders.AbstractRepositoryBuilder;
import es.jcyl.ita.formic.repo.media.FileRepository;
import es.jcyl.ita.formic.repo.media.source.FileEntitySource;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FileRepositoryBuilder extends AbstractRepositoryBuilder<FileEntitySource, FileRepository> {

    public FileRepositoryBuilder(RepositoryFactory repoFactory) {
        super(repoFactory);
    }

    @Override
    protected FileRepository doBuild() {
        File baseFolder = getSource().getBaseFolder();
        if (!baseFolder.exists()) {
            throw new IllegalArgumentException(String.format("The base folder of the " +
                            "FileEntitySource [%s] does not exists, cannot create RepositoryInstance.",
                    baseFolder.getAbsolutePath()));
        }
        return new FileRepository(baseFolder);
    }
}
