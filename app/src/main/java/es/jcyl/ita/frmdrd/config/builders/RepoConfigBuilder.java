package es.jcyl.ita.frmdrd.config.builders;
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

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.frmdrd.config.Config;
import es.jcyl.ita.frmdrd.config.meta.TagDef;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.config.repo.RepoConfig;
import es.jcyl.ita.frmdrd.config.repo.RepositoryConfReader;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class RepoConfigBuilder extends AbstractComponentBuilder<RepoConfig> {
    private static RepositoryConfReader repoReader = Config.getRepoConfigReader();

    public RepoConfigBuilder(String tagName) {
        super(tagName, RepoConfig.class);
    }

    @Override
    protected void doWithAttribute(RepoConfig element, String name, String value) {

    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<RepoConfig> node) {
        // check if there a direct repository definition with dbFile and dbTable attributes
        String dbFile = node.getAttribute("dbFile");
        String dbTable = node.getAttribute("dbTable");
        boolean isDbFileSet = StringUtils.isNotBlank(dbFile);
        boolean isTableNameSet = StringUtils.isNotBlank(dbTable);

        Repository repo = null;
        if (isDbFileSet ^ isTableNameSet) {
            error(String.format("Incorrect repository definition, both 'dbFile' and 'dbTable' " +
                    "must be set in tag ${tag} id[%s.", node.getId()));
        } else if (isDbFileSet && isTableNameSet) {
            // try to create a repository from current configuration
            repo = repoReader.createFromFile(dbFile, dbTable);
        }

        // find first parent that admits "repo" attribute and if doesn't have a repo already defined by
        // attribute "repo", set current repo to it
        ConfigNode parent = findRepoParent(node);
        if (!parent.hasAttribute("repo")) {
            parent.setAttribute("repo", repo.getId());
            UIBuilderHelper.setElementValue(parent.getElement(), "repo", repo);
        }
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<RepoConfig> node) {

    }

    /**
     * Finds first parent element that supports "repo" attribute
     *
     * @param node
     * @return
     */
    private ConfigNode findRepoParent(ConfigNode<RepoConfig> node) {
        ConfigNode parent = node.getParent();
        if (parent == null) {
            return null;
        } else {
            while (parent != null) {
                if (TagDef.supportsAttribute(parent.getName(), "repo")) {
                    return parent;
                }
                parent = parent.getParent();
            }
            return null;
        }
    }


}
