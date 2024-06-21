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

import org.mini2Dx.collections.CollectionUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.builders.ComponentBuilderFactory;
import es.jcyl.ita.formic.forms.config.reader.ConfigReadingInfo;
import es.jcyl.ita.formic.forms.project.handlers.ContextConfigHandler;
import es.jcyl.ita.formic.forms.project.handlers.DefaultImageRepositoryHandler;
import es.jcyl.ita.formic.forms.project.handlers.FormConfigHandler;
import es.jcyl.ita.formic.forms.project.handlers.ProjectResourceHandler;
import es.jcyl.ita.formic.forms.project.handlers.RepoConfigHandler;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.query.BaseFilter;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class ProjectManager {

    private static Map<ProjectResource.ResourceType, ProjectResourceHandler> _handlers = new HashMap<ProjectResource.ResourceType, ProjectResourceHandler>();

    private static ConfigReadingInfo readingListener = new ConfigReadingInfo();
    private final ProjectRepository projectRepo;
    private FormConfigRepository formConfigRepo;
    private Project currentProject;


    private CompositeContext globalContext;

    public ProjectManager(File file) {
        projectRepo = new ProjectRepository(file);
        registerHandlers();
    }

    public void openProject(final Project project) {
        try {
            currentProject = project;
            readConfig(project);
        } catch (Exception e) {
            throw new ConfigurationException(DevConsole.error("Error while trying to open project.", e), e);
        }
    }

    public void closeProject() {
        if(this.formConfigRepo!=null){
            this.formConfigRepo.close();
        }
        this.currentProject = null;
    }

    /**
     * DO NOT USE THIS METHOD, call setCurrentProject instead. Made public just for testing
     * purposes.
     *
     * @param project
     */
    private void readConfig(Project project) {
        if (!project.isOpened()) {
            project.open();
        }
        // clear all previous configs
        clear();

        // update project context
        populateProjectContext(project);

        readingListener.setProject(project);
        DevConsole.setConfigReadingInfo(readingListener);

        // create new formConfig repo to hold current project configuration (forms.xml
        // configurations of current project)
        formConfigRepo = new FormConfigRepository(project);
        FormConfigHandler formConfigHandler = (FormConfigHandler) _handlers.get(ProjectResource.ResourceType.FORM);
        formConfigHandler.setFormConfigRepo(formConfigRepo);

        // share current reading process info using componentBuilderFactory
        ComponentBuilderFactory.getInstance().setInfo(readingListener);

        processProjectResources(project);
    }

    /**
     * Updates project context with current project
     *
     * @param project
     */
    private void populateProjectContext(Project project) {
        // Create project and app contexts and add them to Global context
        BasicContext projectCtx = new BasicContext("project");
        projectCtx.put("folder", project.getBaseFolder());
        projectCtx.put("name", project.getName());
        projectCtx.put("dataFolder", project.getDataFolder());
        projectCtx.put("formsFolder", project.getFormsFolder());
        projectCtx.put("picturesFolder", project.getPicturesFolder());
        globalContext.addContext(projectCtx);
    }

    private static final ProjectResource.ResourceType[] ORDERED_RESOURCES =
            {ProjectResource.ResourceType.CONTEXT, ProjectResource.ResourceType.REPO,
                    ProjectResource.ResourceType.FORM};

    /**
     * Reads the project config files calling the proper handler for each resource and assuring
     * the right processing order: repo > forms > security
     *
     * @param project
     */
    private void processProjectResources(Project project) {
        // check configFiles exist
        List<ProjectResource> configFiles = project.getConfigFiles();
        if (CollectionUtils.isEmpty(configFiles)) {
            throw new ConfigurationException(
                    DevConsole.error(String.format("Couldn't find any config file in project [%s], " +
                            "check the folder.", project.getBaseFolder())));
        } else {
            // process files in RESOURCE_ORDER order (configuration flow)
            for (ProjectResource.ResourceType resType : ORDERED_RESOURCES) {
                configFiles = project.getConfigFiles(resType);
                // TODO: Create class ProjectResources to handle files, projectTemplates, etc. #204283
                // TODO: do we need additional events? (post repo creation, post form creation....)
                for (ProjectResource resource : configFiles) {
                    processResource(resource);
                }
                // anything to do after resource-type files treatment?
                onAfterResourceTypeReading(project, resType);
            }
        }
    }

    public void processResource(ProjectResource resource) {
        readingListener.fileStart(resource.file.getAbsolutePath());
        DevConsole.info("Processing file '${file}'");
        // get a reader for the file and a register for the resulting config object
        _handlers.get(resource.type).handle(resource);
        readingListener.fileEnd(resource.file.getAbsolutePath());
    }

    /**
     * Specific actions to perform after a specific resource type files have been treated.
     *
     * @param resType
     */
    private void onAfterResourceTypeReading(Project project, ProjectResource.ResourceType resType) {
        if (resType == ProjectResource.ResourceType.REPO) {
            // Create default image repository for current project
            DefaultImageRepositoryHandler handler = new DefaultImageRepositoryHandler();
            ProjectResource pr = new ProjectResource(project, null, null);
            handler.handle(pr);
        }
    }

    public void clear() {
        // clear defined forms configs, form controllers and repos
        if (formConfigRepo != null) {
            this.formConfigRepo.deleteAll();
        }
    }

    public CompositeContext getGlobalContext() {
        return globalContext;
    }

    public void setGlobalContext(CompositeContext globalContext) {
        this.globalContext = globalContext;
    }

    /**
     * Register instances responsible for reading each XML file type (form/data).
     */
    private void registerHandlers() {
        //TODO: add additional resource handlers (synchronization, security, etc.)
        ProjectResourceHandler handler = new FormConfigHandler();
        handler.setListener(readingListener);
        _handlers.put(ProjectResource.ResourceType.FORM, handler);
        handler = new RepoConfigHandler();
        handler.setListener(readingListener);
        _handlers.put(ProjectResource.ResourceType.REPO, handler);
        handler = new ContextConfigHandler();
        _handlers.put(ProjectResource.ResourceType.CONTEXT, handler);
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public ProjectRepository getProjectRepo() {
        return projectRepo;
    }

    public FormConfigRepository getFormConfigRepo() {
        return formConfigRepo;
    }

    public String getCurrentBaseFolder() {
        return (currentProject == null) ? null : currentProject.getBaseFolder();
    }

    public void loadFile(String path) {
    }

}
