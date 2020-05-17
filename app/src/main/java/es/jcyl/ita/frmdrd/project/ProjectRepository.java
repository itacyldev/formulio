package es.jcyl.ita.frmdrd.project;
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

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.EditableRepository;
import es.jcyl.ita.crtrepo.EntitySource;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.context.Context;
import es.jcyl.ita.crtrepo.meta.EntityMeta;
import es.jcyl.ita.crtrepo.meta.PropertyType;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ProjectRepository implements EditableRepository<Project, String, BasicFilter> {

    private final EntityMeta projectMeta;
    private final EntitySource projectSource;
    private File baseFolder;

    public ProjectRepository(File baseFolder) {
        this.baseFolder = baseFolder;
        this.projectMeta = createMeta();
        this.projectSource = createSource();
    }

    @Override
    public String getId() {
        return "projects";
    }

    @Override
    public List<Project> find(BasicFilter projectFilter) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public long count(BasicFilter projectFilter) {
        return find(projectFilter).size();
    }

    @Override
    public List<Project> listAll() {
        //TODO: this is just a prototype 204142
        // return all folders as projects using folder names a id,name
        File[] folders = getProjectDirectories();
        List<Project> lst = new ArrayList();
        Project prj;
        if(ArrayUtils.isNotEmpty(folders)){
            for (File folder : folders) {
                prj = createFromFolder(folder);
                lst.add(prj);
            }
        }
        return lst;

    }

    public Project createFromFolder(File folder) {
        Project prj = new Project(projectSource, projectMeta);
        prj.set("baseFolder", folder.getAbsolutePath());
        return prj;
    }

    private File[] getProjectDirectories() {
        File[] directories = baseFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        return directories;
    }

    @Override
    public EntitySource getSource() {
        return this.projectSource;
    }

    @Override
    public EntityMeta getMeta() {
        return this.projectMeta;
    }

    @Override
    public void setContext(Context context) {

    }

    @Override
    public Object getImplementor() {
        return null;
    }

    @Override
    public Class<BasicFilter> getFilterClass() {
        return null;
    }



    private EntitySource createSource() {
        return new EntitySource() {
            @Override
            public String getSourceId() {
                return baseFolder.getAbsolutePath();
            }

            @Override
            public String getEntityTypeId() {
                return "project";
            }
        };
    }

    private EntityMeta createMeta() {
        List<PropertyType> propList = new ArrayList<>();
        propList.add(new PropertyType("id", String.class, "", true));
        propList.add(new PropertyType("name", String.class, "", false));
        propList.add(new PropertyType("description", String.class, "", false));
        propList.add(new PropertyType("baseFolder", String.class, "", false));

        EntityMeta meta = new EntityMeta<>("project",
                propList.toArray(new PropertyType[propList.size()]), new String[]{"id"});
        return meta;
    }

    @Override
    public Project findById(String s) {
        return null;
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public void save(Project project) {

    }

    @Override
    public void delete(Project project) {

    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void deleteAll() {

    }
}
