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

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import org.mini2Dx.collections.CollectionUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.crtrepo.source.EntitySourceFactory;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.config.builders.ComponentBuilderFactory;
import es.jcyl.ita.frmdrd.config.reader.ConfigReadingInfo;
import es.jcyl.ita.frmdrd.forms.FormControllerFactory;
import es.jcyl.ita.frmdrd.project.FormConfigRepository;
import es.jcyl.ita.frmdrd.project.Project;
import es.jcyl.ita.frmdrd.project.ProjectRepository;
import es.jcyl.ita.frmdrd.project.ProjectResource;
import es.jcyl.ita.frmdrd.project.handlers.DefaultImageRepositoryHandler;
import es.jcyl.ita.frmdrd.project.handlers.FormConfigHandler;
import es.jcyl.ita.frmdrd.project.handlers.ProjectResourceHandler;
import es.jcyl.ita.frmdrd.project.handlers.RepoConfigHandler;

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
    private Context andContext;

    private Project currentProject;
    /**
     * Reads project list
     */
    private ProjectRepository projectRepo;
    /**
     * Stores current project form configurations (each entity form setting).
     */
    private FormConfigRepository formConfigRepo;
    /**
     * Stores formControllers instances
     */
    private FormControllerFactory formControllerFactory = FormControllerFactory.getInstance();

    private static ConfigReadingInfo readingListener = new ConfigReadingInfo();

    private Config(String appBaseFolder) {
        this.appBaseFolder = appBaseFolder;
    }

    private Config(Context androidContext, String appBaseFolder) {
        this.appBaseFolder = appBaseFolder;
        this.andContext = androidContext;
    }

    public static Config getInstance() {
        if (_instance == null) {
            throw new ConfigurationException("You first have to call to init method giving " +
                    "the base folder of the project you want to read.");
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
        return _instance;
    }

    public static Config init(Context and, String appBaseFolder) {
        // TODO: cache??
        _instance = new Config(and, appBaseFolder);
        _instance.init();
        return _instance;
    }

    private void init() {
        if (!configLoaded) {
            // customize data type converters
            ConfigConverters confConverter = new ConfigConverters();
            confConverter.init();
            // project repository
            projectRepo = new ProjectRepository(new File(this.appBaseFolder));
            registerReaders();
            configLoaded = true;
        }
    }

    private void clear() {
        // clear defined forms configs, form controllers and repos
        if (formConfigRepo != null) {
            this.formConfigRepo.deleteAll();
        }
        formControllerFactory.clear();
        RepositoryFactory.getInstance().clear();
        EntitySourceFactory.getInstance().clear();
    }

    /**
     * Register instances responsible for reading each XML file type (form/data).
     */
    private static void registerReaders() {
        //TODO: add additional resource handlers (synchronization, security, etc.)
        ProjectResourceHandler reader = new FormConfigHandler();
        reader.setListener(readingListener);
        _handlers.put(ProjectResource.ResourceType.FORM, reader);
        reader = new RepoConfigHandler();
        reader.setListener(readingListener);
        _handlers.put(ProjectResource.ResourceType.REPO, reader);
    }

    public void readConfig(Project project) {
        if (!project.isOpened()) {
            project.open();
        }
        // clear all previous configs
        clear();

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

    private static final ProjectResource.ResourceType[] RESOURCE_ORDER =
            {ProjectResource.ResourceType.REPO, ProjectResource.ResourceType.FORM};

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
            // process files in RESOURCE_ORDER order
            for (ProjectResource.ResourceType resType : RESOURCE_ORDER) {
                configFiles = project.getConfigFiles(resType);

                // TODO: Create class ProjectResources to handle files, projectTemplates, etc. #204283
                // TODO: do we need additional events? (post repo creation, post form creation....)
                for (ProjectResource resource : configFiles) {
                    readingListener.setCurrentFile(resource.file.getAbsolutePath());
                    DevConsole.info("Processing file '${file}'");
                    // get a reader for the file and a register for the resulting config object
                    _handlers.get(resource.type).handle(resource);
                }
                // anything to do after resource-type files treatment?
                onAfterResourceTypeReading(project, resType);
            }
        }
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


    /**
     * Common repositories
     *
     * @return
     */
    public ProjectRepository getProjectRepo() {
        return projectRepo;
    }

    /**
     * Read the configuration of the selected project as current. If there are problems do not set it.
     *
     * @param context
     * @param project Selected project.
     */
    public void setCurrentProject(final Context context, @NonNull final Project project) {
        Toast.makeText(context, DevConsole.info(context.getString(R.string.opening_project) + project.getId()), Toast.LENGTH_LONG).show();
        try {
            readConfig(project);
            currentProject = project;
            debugConfig();
        } catch (Exception e) {
            DevConsole.error("Error while trying to open project.", e);
            Toast.makeText(context, "An error occurred while trying to read your projects. See console for details",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Read the configuration of the selected project as current. If there are problems do not set it.
     */
    private void debugConfig() {
        RepositoryFactory repoFactory = RepositoryFactory.getInstance();
        Set<String> repoIds = repoFactory.getRepoIds();
        DevConsole.info("Repos registered: " + repoIds);
    }

    public Project getCurrentProject() {
        return this.currentProject;
    }

    public RepoConfigHandler getRepoConfigReader() {
        return (RepoConfigHandler) _handlers.get(ProjectResource.ResourceType.REPO);
    }

    public FormConfigRepository getFormConfigRepo() {
        return formConfigRepo;
    }

    public Resources getResources() {
        return this.andContext.getResources();
    }

}
