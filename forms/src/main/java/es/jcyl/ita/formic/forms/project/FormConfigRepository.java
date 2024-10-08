package es.jcyl.ita.formic.forms.project;
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

import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.repo.AbstractEditableRepository;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.EntityMapping;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.query.BaseFilter;
import es.jcyl.ita.formic.repo.source.EntitySource;
import es.jcyl.ita.formic.repo.source.Source;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FormConfigRepository extends AbstractEditableRepository<FormConfig, String, BaseFilter> {

    private final Project project;
    private final EntitySource source;
    private final EntityMeta meta;

    private Map<String, FormConfig> memoryRepo = new HashMap<>();

    public FormConfigRepository(Project project) {
        this.project = project;
        this.meta = createMeta();
        this.source = createSource();
    }

    public List<FormConfig> findByDefinitionFile(String formXMLPath) {
        // finds all the formConfig defined in the given file path
        List<FormConfig> configs = new ArrayList<>();
        for (FormConfig formConfig : this.memoryRepo.values()) {
            if (formXMLPath.endsWith(formConfig.getFilePath())) {
                configs.add(formConfig);
            }
        }
        return configs;
    }

    @Override
    public FormConfig doFindById(String s) {
        return memoryRepo.get(s);
    }

    @Override
    public void delete(FormConfig formConfig) {
        deleteById(formConfig.getId());
    }

    @Override
    protected void doDelete(FormConfig entity) {

    }

    @Override
    public void save(FormConfig formConfig) {
        memoryRepo.put(formConfig.getId(), formConfig);
    }

    @Override
    protected void doSave(FormConfig entity) {

    }

    @Override
    public void deleteById(String s) {
        this.memoryRepo.remove(s);
    }

    @Override
    protected void doDeleteById(String key) {

    }

    @Override
    public void deleteAll() {
        this.memoryRepo.clear();
    }

    @Override
    protected void doDeleteAll() {

    }

    @Override
    public FormConfig newEntity() {
        return new FormConfig(this.source, this.meta);
    }

    @Override
    public String getId() {
        return "formConfig";
    }

    @Override
    public List<FormConfig> find(BaseFilter basicFilter) {
        throw new NotImplementedException("Not implemeted!");
    }

    @Override
    protected List<FormConfig> doFind(BaseFilter filter) {
        return null;
    }

    @Override
    public long count(BaseFilter basicFilter) {
        return memoryRepo.size();
    }

    @Override
    public List<FormConfig> listAll() {
        return new ArrayList<>(memoryRepo.values());
    }

    @Override
    protected List<FormConfig> doListAll() {
        return null;
    }

    @Override
    public EntitySource getSource() {
        return this.source;
    }

    @Override
    public EntityMeta doGetMeta() {
        return this.meta;
    }

    @Override
    public Object getImplementor() {
        return null;
    }

    @Override
    public Class<BaseFilter> getFilterClass() {
        return BaseFilter.class;
    }

    @Override
    public void setMappings(List<EntityMapping> mappings) {

    }

    @Override
    public List<EntityMapping> getMappings() {
        return null;
    }

    @Override
    public void addMapping(EntityMapping mapping) {

    }

    @Override
    public void close() {
        memoryRepo.clear();
    }


    private EntitySource createSource() {
        String baseFolder = project.getBaseFolder();
        return new EntitySource() {
            @Override
            public String getSourceId() {
                return baseFolder;
            }

            @Override
            public Source getSource() {
                return new Source(baseFolder, baseFolder, baseFolder);
            }

            @Override
            public String getEntityTypeId() {
                return "formConfig";
            }
        };
    }

    private EntityMeta createMeta() {
        List<PropertyType> propList = new ArrayList<>();
        propList.add(new PropertyType("id", String.class, "", true));
        propList.add(new PropertyType("name", String.class, "", false));
        propList.add(new PropertyType("description", String.class, "", false));
        propList.add(new PropertyType("filePath", String.class, "", false));

        EntityMeta meta = new EntityMeta<>("formConfig",
                propList.toArray(new PropertyType[propList.size()]), new String[]{"id"});
        return meta;
    }

}
