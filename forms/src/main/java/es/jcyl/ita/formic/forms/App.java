package es.jcyl.ita.formic.forms;
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

import org.apache.commons.io.FileUtils;

import java.io.File;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.context.impl.DateTimeContext;
import es.jcyl.ita.formic.forms.context.impl.RepoAccessContext;
import es.jcyl.ita.formic.forms.controllers.ViewControllerFactory;
import es.jcyl.ita.formic.forms.jobs.reader.RepoReader;
import es.jcyl.ita.formic.forms.location.LocationService;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectManager;
import es.jcyl.ita.formic.forms.project.ProjectRepository;
import es.jcyl.ita.formic.forms.view.dag.DAGManager;
import es.jcyl.ita.formic.jayjobs.jobs.JobFacade;
import es.jcyl.ita.formic.jayjobs.jobs.listener.JobExecListener;
import es.jcyl.ita.formic.jayjobs.task.config.TaskConfigFactory;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.source.EntitySourceFactory;


/**
 * Configuration initializer and common point to store and share configuration parameters.
 * <p>
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class App {
    private static App _instance;

    private boolean configLoaded = false;
    private String appBaseFolder;
    private Context andContext;
    private CompositeContext globalContext;

    private JobFacade jobFacade;
    private ProjectManager projectManager;

    /**
     * Stores current project form configurations (each entity form setting).
     */
    /**
     * Stores formControllers instances
     */
    private ViewControllerFactory formControllerFactory = ViewControllerFactory.getInstance();

    private App(String appBaseFolder) {
        this.appBaseFolder = appBaseFolder;
    }

    private App(Context androidContext, String appBaseFolder) {
        this.appBaseFolder = appBaseFolder;
        this.andContext = androidContext;
    }

    public static App getInstance() {
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
    public static App init(String appBaseFolder) {
        _instance = new App(appBaseFolder);
        _instance.init();
        return _instance;
    }

    public static App init(Context and, String appBaseFolder) {
        // TODO: cache??
        _instance = new App(and, appBaseFolder);
        _instance.init();
        return _instance;
    }

    private void init() {
        if (!configLoaded) {
            // initialize global context and set to ContextAware components
            initContext();
            // customize data type converters
            ConfigConverters confConverter = new ConfigConverters();
            confConverter.init();
            // setup project manager
            projectManager = new ProjectManager(new File(this.appBaseFolder));
            projectManager.setGlobalContext(globalContext);
            // configure job facade
            jobFacade = new JobFacade();
            configLoaded = true;
            registerRepoReader();
        }
    }

    /**
     * Create globalContext and set it to all ContextAwareComponents
     * //TODO: manage with dependency injection
     */
    private void initContext() {
        globalContext = new UnPrefixedCompositeContext();
        // TODO: create context providers to implement each context creation and configure
        //  them in project file
        globalContext.addContext(new DateTimeContext());
        globalContext.addContext(new BasicContext("session"));
        // TODO: configure context and default sync properties in XML in res folder
        if (this.andContext != null) {
            this.globalContext.put("location", new LocationService(this.andContext));
        }
        this.globalContext.put("repos", new RepoAccessContext());

        initJobsContext(globalContext);
        // set context to context dependant objects
        MainController.getInstance().setContext(globalContext);
        RepositoryFactory.getInstance().setContext(globalContext);
    }

    /**
     * Prepares temp execution folder and context information to execute jobs.
     *
     * @param ctx
     */
    private void initJobsContext(CompositeContext ctx) {
        // Create temporary directory for job execution if it doesn't already exists
        File osTempDirectory = FileUtils.getTempDirectory();
        if (!osTempDirectory.exists()) {
            // use cache dir
            osTempDirectory = andContext.getCacheDir();
        }
        File tmpFolder = new File(osTempDirectory, "tmp");
        if (!tmpFolder.exists()) {
            tmpFolder.mkdir();
        }
        // application context
        BasicContext appCtx = new BasicContext("app");
        appCtx.put("workingFolder", tmpFolder.getAbsolutePath());
        globalContext.addContext(appCtx);
    }


    private static void registerRepoReader() {
        TaskConfigFactory factory = TaskConfigFactory.getInstance();
        factory.addTaskStep("repoReader", RepoReader.class);
    }

    /**
     * Read the configuration of the selected project as current. If there are problems do not set it.
     *
     * @param project Selected project.
     */
    public void openProject(final Project project) {
        try {
            projectManager.openProject(project);
        } catch (Exception e) {
            throw new ConfigurationException(DevConsole.error("Error while trying to open project.", e), e);
        }
    }

    public void clear() {
        // clear defined forms configs, form controllers and repos
        projectManager.clear();
        formControllerFactory.clear();
        MainController.getInstance().clear();
        RepositoryFactory.getInstance().clear();
        EntitySourceFactory.getInstance().clear();
        this.globalContext.clear();
        DAGManager.getInstance().flush();
    }

    /******** GETTERS/SETTERS *******/
    /**
     * Common repositories
     *
     * @return
     */
    public ProjectRepository getProjectRepo() {
        return projectManager.getProjectRepo();
    }

    public RepositoryFactory getRepoFactory() {
        return RepositoryFactory.getInstance();
    }

    public ProjectManager getProjectManager() {
        return projectManager;
    }

    public Project getCurrentProject() {
        return this.projectManager.getCurrentProject();
    }

    public String getCurrentBaseFolder() {
        return this.projectManager.getCurrentProject().getBaseFolder();
    }

    public Resources getResources() {
        return this.andContext.getResources();
    }

    public CompositeContext getGlobalContext() {
        return globalContext;
    }

    public void setGlobalContext(CompositeContext globalContext) {
        this.globalContext = globalContext;
    }

    public Context getAndroidContext() {
        return this.andContext;
    }

    public String getStringResource(int stringId) {
        return (this.getAndroidContext() == null) ? null : getResources().getString(stringId);
    }

    public JobFacade getJobFacade() {
        return jobFacade;
    }

    public void setJobFacade(JobFacade jobFacade) {
        this.jobFacade = jobFacade;
    }

    public void setJobListener(JobExecListener jobListener) {
        this.jobFacade.setListener(jobListener);
    }
}
