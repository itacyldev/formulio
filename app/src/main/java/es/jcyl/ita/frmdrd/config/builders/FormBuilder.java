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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.builders.FormEditBuilder;
import es.jcyl.ita.frmdrd.config.reader.BaseConfigNode;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;

import static es.jcyl.ita.frmdrd.config.ConfigConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
class FormBuilder extends GroupingBuilder {

    protected RepositoryFactory repoFactory = RepositoryFactory.getInstance();

    private UIForm baseModel;

    public FormBuilder(String tagName) {
        super("form");
        baseModel = new UIForm();
    }


    public FormBuilder withRepo(Repository repo) {
        this.baseModel.setRepo(repo);
        return this;
    }

    public FormBuilder withRepo(String repoId) {
        Repository repo = this.repoFactory.getRepo(repoId);
        withRepo(repo);
        return this;
    }


    public FormBuilder withFilter(Filter filter) {
        this.baseModel.setFilter(filter);
        return this;
    }


    protected void setRepository() {
        // check if there's a nested data tag with repos and process it
        //TODO: include

        // make sure the repository is uniquely defined
        boolean defined = false;
        String repoAtt = this.getAttribute("repo");
        if (StringUtils.isNotBlank(repoAtt)) {
            // find repository
            Repository repo = this.repoFactory.getRepo(repoAtt);
            if (repo == null) {
                error(String.format("Invalid repo Id found: [%s] in form [%s].", repoAtt, this.id));
            } else {
                withRepo(repo);
                defined = true;
            }
        }

        // check if there a direct repository definition with dbFile and dbTable attributes
        String dbFile = this.getAttribute("dbFile");
        String dbTable = this.getAttribute("dbTable");
        if (StringUtils.isNotBlank(dbFile) || StringUtils.isNotBlank(dbTable)) {
            if (defined) {
                error(String.format("Repository is already defined with attribute 'repo' but a new " +
                                "definition is found with attributes dbFile and dbTable in form [%s]."
                        , this.id));
            } else {
                // try to create a repository from current configuration

                defined = true;
            }
        }
        if (!defined) {
            error(String.format("No proper repository configuration in form [%s], check " +
                    "configuration file. ", this.id));
        }
    }

    private void setFilter() {
        List<ConfigNode> filters = this.getChildren("repofilter");
        if (filters.size() > 1) {
            error(String.format("Error in form [%s], just one repofilter tag can be used.", this.id));
        } else if (filters.size() == 1) {
            withFilter((Filter) filters.get(0).getElement());
        }
    }

    private void setComponents() {
        this.baseModel.setChildren(this.getUIComponents());
    }


    /**
     * Creates form uicomponents automatically from repository meta
     */
    private void setAutoFields() {
        // if properties attribute is set use it to select repo fields
        String propertySelector = this.getAttribute("properties");

        Repository repo = this.baseModel.getRepo();
        EntityMeta meta = repo.getMeta();
        PropertyType[] properties;

        boolean all = "*".equals(propertySelector.trim()) || "all".equals(propertySelector.trim().toLowerCase());
        if (all) {
            properties = meta.getProperties();
        } else {
            // select properties by name
            StringTokenizer stk = new StringTokenizer(propertySelector, ",");
            properties = new PropertyType[stk.countTokens()];
            int i = 0;
            PropertyType prop;
            while (stk.hasMoreElements()) {
                String propName = stk.nextToken();
                prop = meta.getPropertyByName(propName);
                if (prop == null) {
                    error(String.format("No property found [%s] in meta from [%s] repository. " +
                            "Available properties: [%s].", propName, repo.getId(), meta.getPropertyNames()));
                } else {
                    properties[i] = prop;
                }
                i++;
            }
        }




        // create fields from properties
        List<UIComponent> kids = new ArrayList<>();

        for (int i = 0; i < properties.length; i++) {
            kids[i] = fieldBuilder.withProperty(properties[i]).build();
        }


        this.baseModel.setChildren(kids);

    }

    public ConfigNode build() {
        setRepository();
        setFilter();
        setComponents();

        // if no nested field is defined, create from repository meta with 'fields' attribute
        setAutoFields();

        UIForm model = baseModel;
        this.baseModel = new UIForm();


        BaseConfigNode node = new BaseConfigNode();
        node.setName("edit");
        node.setElement(model);
        return node;
    }

}
