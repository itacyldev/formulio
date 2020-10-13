package es.jcyl.ita.formic.repo.config;
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

import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.source.EntitySource;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.repo.meta.Identificable;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Base class that gathers the information in a form configuration file.
 */

public class FormConfig extends Entity implements Identificable {

    private String id;
    private String name;
    private String description;
    private String filePath;
    private FormListController list;
    private List<FormEditController> edits;
    private Repository repo;

    public FormConfig(){
        super(null, null);
    }

    public FormConfig(EntitySource source, EntityMeta meta) {
        super(source, meta);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FormListController getList() {
        return list;
    }

    public void setList(FormListController list) {
        this.list = list;
    }

    public List<FormEditController> getEdits() {
        return edits;
    }

    public void setEdits(List<FormEditController> edits) {
        this.edits = edits;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
this.id = id;
    }

    public void setId(Object id) {
        this.id = (String)id;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }

    public Repository getRepo() {
        return repo;
    }
}
