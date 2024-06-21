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

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.elements.RepoConfig;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.repo.EntityMapping;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.db.sqlite.meta.SQLiteMetaModeler;
import es.jcyl.ita.formic.repo.meta.PropertyType;

/**
 * Creates and entity mapping in the parent repository from XML configuration
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class EntityMappingBuilder extends AbstractComponentBuilder<EntityMapping> {
    private SQLiteMetaModeler modeler = new SQLiteMetaModeler();

    public EntityMappingBuilder(String tagName) {
        super(tagName, EntityMapping.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<EntityMapping> node) {
        // get current repo source
        Object obj = node.getParent().getElement();
        if (!(obj instanceof RepoConfig)) {
            throw new ConfigurationException(DevConsole.error("The <mapping/> element must be nested" +
                    " directly under a <repo/> element. Error found in file ${file}."));
        }
        RepoConfig repoConfig = (RepoConfig) obj;
        Repository repo = getFactory().getRepoFactory().getRepo(repoConfig.getId());
        EntityMapping mapping = node.getElement();
        // check fk property
        if (StringUtils.isBlank(mapping.getFk())) {
            throw new ConfigurationException(DevConsole.error(String.format("Error while setting " +
                            "entityMapping for repo [%s] referencing repo [%s], the attribute 'fk' " +
                            "must be set providing a property of the main repo or a JEXL expression.",
                    repo.getId(), node.getAttribute("repo"))));
        } else if (!mapping.isFkExpression()) {
            // if the mapping is base on a property of the main repo, check if it exists
            PropertyType fkProperty = repo.getMeta().getPropertyByName(mapping.getFk());
            if (fkProperty == null) {
                throw new ConfigurationException(DevConsole.error(String.format("Error while setting " +
                                "entityMapping for repo [%s] referencing repo [%s], the property set as " +
                                "fk reference [%s] doesn't exists in [%s]. ${file}]}.", repo.getId(),
                        node.getAttribute("repo"), mapping.getFk(), repo.getId())));
            }
        }
        // check property name
        if (StringUtils.isBlank(mapping.getProperty())) {
            throw new ConfigurationException(DevConsole.error(String.format("Error while setting " +
                            "entityMapping for repo [%s] referencing repo [%s], the attribute 'property' " +
                            "must be set providing the name of the property that will hold the related" +
                            "entity.",
                    repo.getId(), node.getAttribute("repo"))));
        } else {
            // the name of the transient property musn't overlap with an existing property name
            if (repo.getMeta().getPropertyByName(mapping.getProperty()) != null) {
                throw new ConfigurationException(DevConsole.error(String.format("Error while setting " +
                                "entityMapping for repo [%s] referencing repo [%s], there is already " +
                                "a property called [%s] in repo [%s], change the value of attribute " +
                                "'property'.",
                        repo.getId(), node.getAttribute("repo"), mapping.getProperty(), repo.getId())));
            }
        }
        repo.addMapping(mapping);
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<EntityMapping> node) {
        // Do nothing
    }
}