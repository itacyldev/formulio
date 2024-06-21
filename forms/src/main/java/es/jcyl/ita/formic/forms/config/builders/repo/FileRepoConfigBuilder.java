package es.jcyl.ita.formic.forms.config.builders.repo;
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

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.elements.RepoConfig;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.repo.builders.RepositoryBuilder;
import es.jcyl.ita.formic.repo.media.FileRepository;
import es.jcyl.ita.formic.repo.media.source.FileEntitySource;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * Builder to create FileRepository definition from xml.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class FileRepoConfigBuilder extends AbstractComponentBuilder<RepoConfig> {

    public FileRepoConfigBuilder(String tagName) {
        super(tagName, RepoConfig.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<RepoConfig> node) {
        if (!node.hasAttribute("folder")) {
            throw new ConfigurationException(error(String.format("Error while trying to create " +
                            "fileRepo element with id [%s], the attribute 'folder' must be " +
                            "provided.",
                    node.getId())));
        }

        String baseFolder = node.getAttribute("folder");
        File baseFile = new File(baseFolder);
        if (baseFile.isAbsolute()) {
            if (!baseFile.exists()) {
                throw new ConfigurationException(error(String.format("Error while trying to create " +
                        "fileRepo element with id [%s], the absolute path [%s] doesn't " +
                        "exists in the device.", node.getId(), baseFile.getAbsolutePath())));
            }
        } else {
            // create under project folder
            String projectFolder = App.getInstance().getCurrentBaseFolder();
            baseFile = new File(projectFolder, baseFolder);
            if (!baseFile.exists()) {
                try {
                    baseFile.mkdirs();
                } catch (Throwable t) {
                    throw new ConfigurationException(error(String.format("Error while trying to create " +
                            "fileRepo element with id [%s], cannot create the path [%s] " +
                            "under project base folder [%s].", node.getId(), baseFolder, projectFolder)));
                }
            }
        }
        RepositoryBuilder builder = getFactory().getRepoFactory().getBuilder(new FileEntitySource(baseFile, node.getId()));
        FileRepository repo = (FileRepository) builder.build();
        repo.setDefaultExtension(node.getAttribute("defaultExtension"));
//        FileRepository repo = new FileRepository(baseFile);
//        this.getFactory().getRepoFactory().register(node.getId(), repo);
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<RepoConfig> node) {
        // Do nothing
    }
}
