package es.jcyl.ita.frmdrd.config.resolvers;
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
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.frmdrd.config.Config;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.config.repo.RepositoryConfReader;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Helper class used during config reading to obtain reference to a repository.
 */
public class RepositoryAttributeResolver {

    private RepositoryConfReader repoReader = Config.getRepoConfigReader();
    private RepositoryFactory repoFactory = RepositoryFactory.getInstance();


    public Repository resolve(ConfigNode node) {
        // make sure the repository is uniquely defined
        boolean defined = false;
        String repoAtt = node.getAttribute("repo");
        if (StringUtils.isNotBlank(repoAtt)) {
            Repository repo = getRepoById(node, repoAtt);
            if (repo != null) {
                return repo;
            }
        }

        // check if there a direct repository definition with dbFile and dbTable attributes
        String dbFile = node.getAttribute("dbFile");
        String dbTable = node.getAttribute("dbTable");
        boolean isDbFileSet = StringUtils.isNotBlank(dbFile);
        boolean isTableNameSet = StringUtils.isNotBlank(dbTable);
        if (isDbFileSet || isTableNameSet) {
            if (defined) {
                error(String.format("Repository is already defined with attribute 'repo' but a new " +
                                "definition is found with attributes dbFile and dbTable in form [%s]."
                        , id(node)));
            } else if (isDbFileSet ^ isTableNameSet) {
                error(String.format("Incorrect repository definition, both 'dbFile' and 'dbTable' " +
                        "must be set in tag {tag} id[%s.", id(node)));
            } else {
                // try to create a repository from current configuration
                return repoReader.createFromFile(dbFile, dbTable);
            }
        }

        // try to find repository Id in an ancestor
        String parentRepoId = findParentRepo(node);
        if (parentRepoId != null) {
            Repository repo = getRepoById(node, parentRepoId);
            return repo;
        }

        return null;
    }

    private Repository getRepoById(ConfigNode node, String repoAtt) {
        // find repository
        Repository repo = this.repoFactory.getRepo(repoAtt);
        if (repo == null) {
            error(String.format("Invalid repo Id found: [%s] in form [%s].", repoAtt, id(node)));
        } else {
            return repo;
        }
        return null;
    }

    /**
     * Goes up in the node hierarchy to find a parent with a "repo" attribute set
     *
     * @param root
     * @return
     */
    private String findParentRepo(ConfigNode root) {
        ConfigNode node = root.getParent();
        String repoId = null;
        while (node != null) {
            repoId = node.getAttribute("repo");
            if (StringUtils.isNotBlank(repoId)) {
                return repoId;
            }
            node = node.getParent();
        }
        return repoId;
    }

    private String id(ConfigNode node) {
        return node.getAttribute("id");
    }


}
