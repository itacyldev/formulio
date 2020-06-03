package es.jcyl.ita.frmdrd.config;
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

import org.mini2Dx.collections.CollectionUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.frmdrd.config.builders.ComponentBuilderFactory;
import es.jcyl.ita.frmdrd.config.reader.ConfigReadingInfo;
import es.jcyl.ita.frmdrd.forms.FormControllerFactory;
import es.jcyl.ita.frmdrd.project.FormConfigRepository;
import es.jcyl.ita.frmdrd.project.Project;
import es.jcyl.ita.frmdrd.project.ProjectRepository;
import es.jcyl.ita.frmdrd.project.ProjectResource;
import es.jcyl.ita.frmdrd.project.handlers.FormConfigHandler;
import es.jcyl.ita.frmdrd.project.handlers.ProjectResourceHandler;
import es.jcyl.ita.frmdrd.project.handlers.RepositoryConfHandler;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Configuration initializer and commons point to store and share configuration parameters.
 */
public class Config {
    private static Config _instance;
    private static Map<ProjectResource.ResourceType, ProjectResourceHandler> _handlers = new HashMap<ProjectResource.ResourceType, ProjectResourceHandler>();

    private boolean configLoaded = false;
    private String appBaseFolder;

    private RepositoryConfHandler repoConfigReader;
    private ProjectRepository projectRepo;
    private FormConfigRepository formConfigRepo;
    private FormControllerFactory formControllerFactory = FormControllerFactory.getInstance();

    private Project currentProject;

    private static ConfigReadingInfo readingListener = new ConfigReadingInfo();

    private Config(String appBaseFolder) {
        this.appBaseFolder = appBaseFolder;
    }

    public static Config getInstance() {
        if (_instance == null) {
            throw new ConfigurationException("You first have to call to init method providing " +
                    "the base folder of the configuration you want to read.");
        }
        return _instance;
    }

    /**
     * Static initialization for
     *
     * @param appBaseFolder
     * @return
     */
    public static Config init(String appBaseFolder) {
        // TODO: cache??
        _instance = new Config(appBaseFolder);
        _instance.init();
        registerReaders();
        return _instance;
    }

    private void init() {
        if (!configLoaded) {
            // customize data type converters
            ConfigConverters confConverter = new ConfigConverters();
            confConverter.init();
            // repository holder
            repoConfigReader = new RepositoryConfHandler();
            // project
            projectRepo = new ProjectRepository(new File(this.appBaseFolder));
            configLoaded = true;
        }
    }

    private void clear() {
        // clear defined forms configs, form controllers and repos
        if (formConfigRepo != null) {
            this.formConfigRepo.deleteAll();
        }
        formControllerFactory.clear();
        repoConfigReader.clear();
    }


    private static void registerReaders() {
        ProjectResourceHandler reader = new FormConfigHandler();
        reader.setListener(readingListener);
        _handlers.put(ProjectResource.ResourceType.FORM, reader);
        reader = new RepositoryConfHandler();
        reader.setListener(readingListener);
        _handlers.put(ProjectResource.ResourceType.DATA, reader);
    }

    public void readConfig(Project project) {
        if (!project.isOpened()) {
            project.open();
        }
        // clear all previous configs
        clear();

        readingListener.setProject(project);
        DevConsole.setConfigReadingInfo(readingListener);

        // create new formConfig repo to hold current project configuration and set to resource handler
        formConfigRepo = new FormConfigRepository(project);
        FormConfigHandler formConfigHandler = (FormConfigHandler) _handlers.get(ProjectResource.ResourceType.FORM);
        formConfigHandler.setFormConfigRepo(formConfigRepo);

        // set current shared into with builder factory
        ComponentBuilderFactory.getInstance().setInfo(readingListener);

        List<ProjectResource> configFiles = project.getConfigFiles();
        if (CollectionUtils.isEmpty(configFiles)) {
            throw new ConfigurationException(
                    DevConsole.error(String.format("Couldn't find any config file in project [%s], " +
                            "check the folder.", project.getBaseFolder())));
        } else {
            // TODO: Create class ProjectResources to handle files, projectTemplates, etc. #204283
            // the data config files have to be read first, know the project is ordering them
            for (ProjectResource resource : configFiles) {
                readingListener.setCurrentFile(resource.file.getAbsolutePath());
                DevConsole.info("Processing file '${file}'");
                // get a reader for the file and a register for the resulting config object
                ProjectResourceHandler handler = _handlers.get(resource.type);
                handler.handle(resource);
            }
        }
    }


    /**
     * Common repositories
     *
     * @return
     */
    public ProjectRepository getProjectRepo() {
        return projectRepo;
    }

    public void setCurrentProject(Project project){
        this.currentProject = project;
    }

    public Project getCurrentProject(){
        return this.currentProject;
    }

    public RepositoryConfHandler getRepoConfigReader() {
        return repoConfigReader;
    }

    public FormConfigRepository getFormConfigRepo() {
        return formConfigRepo;
    }
}
