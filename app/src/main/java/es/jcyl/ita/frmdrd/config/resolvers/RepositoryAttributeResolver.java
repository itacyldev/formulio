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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.frmdrd.config.ConfigNodeHelper;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Helper class used during config reading to obtain reference to a repository.
 */
public class RepositoryAttributeResolver extends AbstractAttributeResolver<Repository> {

    private static final Set<String> INHERIT_PARENT_REPO = new HashSet<String>(Arrays.asList("list", "datatable", "edit", "form"));

    private static RepositoryFactory repoFactory = RepositoryFactory.getInstance();

    public Repository resolve(ConfigNode node, String attName) {
        // make sure the repository is uniquely defined
        String repoAtt = node.getAttribute(attName);
        if (repoAtt == null && INHERIT_PARENT_REPO.contains(node.getName())) {
            // get repository from parent
            repoAtt = ConfigNodeHelper.findParentAtt(node, "repo");
        }

        Repository repo = repoFactory.getRepo(repoAtt);
        if (repo == null) {
            throw new ConfigurationException(
                    error(String.format("Invalid repo Id found: [%s] in file '${file}. Make sure " +
                                    "the id is correct and it's defined in repo.xml file. If the <repo/> " +
                                    "tag is inside current form file, make sure the repo is defined before " +
                                    "current element <${tag}/>",
                            repoAtt)));
        }
        return repo;
    }


}
