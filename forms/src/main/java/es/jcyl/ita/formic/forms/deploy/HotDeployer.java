package es.jcyl.ita.formic.forms.deploy;
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

import android.os.Handler;
import android.os.Looper;

import org.apache.commons.io.FileUtils;
import org.mini2Dx.collections.CollectionUtils;
import org.mini2Dx.collections.IteratorUtils;
import org.mozilla.javascript.Context;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.project.FormConfigRepository;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectManager;
import es.jcyl.ita.formic.forms.project.ProjectResource;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.source.EntitySourceFactory;

/**
 * Detects changes in project configuration files and reload configuration objects. If change affects to current view,
 * asks de MainController to re-render it.
 *
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class HotDeployer {

    private static final long DELAY_MS = 5_000;
    private static final long TIME_BETWEEN_CHECKS_MS = 2_500;

    private final MainController mc;
    private final ProjectManager projectManager;
    private final RedeployChanges changes;
    private final Handler uiHandler;
    private String baseFolder;
    private FileWatcher worker;
    private long lastExecution = System.currentTimeMillis();
    private Map<String, Long> filesModTimes = new HashMap<>();

    public HotDeployer(MainController mc, ProjectManager projectManager) {
        this.projectManager = projectManager;
        this.mc = mc;
        changes = new RedeployChanges(this.mc);
        this.uiHandler = new Handler(Looper.getMainLooper());
    }

    public static boolean isJUnitTest() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                return true;
            }
        }
        return false;
    }


    public void setPath(String path) {
        this.baseFolder = path;
    }


    private List<String> detectFileChanges(String baseFolder) {
        // Android fsystem truncate file modification timestamp to seconds
        long newTimeStamp = 1000 * (System.currentTimeMillis() / 1000); // truncate timestamp
        List<String> lst = new ArrayList<>();
        Iterator<File> data1 = FileUtils.iterateFiles(new File(baseFolder, "data"),
                new String[]{"xml", "sqlite"}, false);
        Iterator<File> data2 = FileUtils.iterateFiles(new File(baseFolder, "forms"),
                new String[]{"xml", "js"}, true);
        Iterator iterator = IteratorUtils.chainedIterator(data1, data2);
        while (iterator.hasNext()) {
            File file = (File) iterator.next();
            if (isModified(file)) {
                DevConsole.info("Change detected in file " + file.getAbsolutePath());
                lst.add(file.getAbsolutePath());
            }
        }
        lastExecution = newTimeStamp;
        return lst;
    }

    private boolean isModified(File file) {
        boolean isModified = false;
        Long lastModified = file.lastModified();
        // get last mod from cache
        Long cachedLastMod = filesModTimes.get(file.getAbsolutePath());
        if (cachedLastMod == null) {
            // no cached value, check against last execution
            isModified = lastModified >= lastExecution;
        } else {
            isModified = lastModified > cachedLastMod;
        }
        if (isModified || cachedLastMod == null) {
            filesModTimes.put(file.getAbsolutePath(), lastModified);
        }
        return isModified;
    }

    public void start() {
        // stop previous thread if exists
        this.stopThread();
        if (!isJUnitTest()) {
            DevConsole.info("Staring file watcher in folder" + this.baseFolder);
            // TODO: cambiar por un servicio en segundo plano que ejecute cuando se invoque desde un intent
            // create thread to observe changes in project files
            worker = new FileWatcher();
            worker.start();
        }
    }

    public void stop() {
        stopThread();
        this.filesModTimes.clear();
    }

    private void stopThread() {
        if (this.worker != null) {
            this.worker.stopWatch();
        }
    }

    private void deployChanges(List<String> paths) {
        changes.classify(paths);
        if (changes.isRepoChanged()) {
            DevConsole.info("Reloading repo definition " + changes.repoPath);
            try {
                reloadRepoDef(changes.repoPath);
            } catch (Exception e) {
                DevConsole.error("An error occurred while trying to reload resource repo.xml. Reload is stopped.", e);
            }
        }
        if (changes.isFormsChanged()) {
            for (String path : changes.forms) {
                DevConsole.info("Reloading form file " + path);
                try {
                    reloadFormDefinition(path);
                } catch (Exception e) {
                    DevConsole.error("An error occurred while trying to reload resource: " + path, e);
                    return;
                }
            }
        }
        if (changes.isJsChanged()) {
            for (String path : changes.js) {
                DevConsole.info("Reloading script file " + path);
                try {
                    reloadScriptFile(path);
                } catch (Exception e) {
                    DevConsole.error("An error occurred while trying to reload resource: " + path, e);
                    return;
                }
            }
        }
        if (changes.requiresRendering) {
            DevConsole.info("Re-rendering current view....");
            reRenderRunnable.setRequiresControllerReload(changes.requiresControllerReload);
            uiHandler.post(reRenderRunnable);
        }
    }

    private void reloadRepoDef(String path) {
        DevConsole.info("Detected modification in repo.xml, reopening project...");
        // clear previous repo and entity sources
        RepositoryFactory.getInstance().clear();
        EntitySourceFactory.getInstance().clear();
        // reopen current project
        Project current = projectManager.getCurrentProject();
        projectManager.closeProject();
        projectManager.openProject(current);
    }

    private void reloadFormDefinition(String path) {
        Project project = App.getInstance().getCurrentProject();
        ProjectResource pResource = new ProjectResource(project, new File(path), ProjectResource.ResourceType.FORM);
        // remove all the form definitions included in this file
        FormConfigRepository formConfigRepo = projectManager.getFormConfigRepo();
        List<FormConfig> formConfigs = formConfigRepo.findByDefinitionFile(path);
        for (FormConfig formConfig : formConfigs) {
            formConfigRepo.delete(formConfig);
        }
        // remove related scripts before reading form definition
        mc.getScriptEngine().clearScriptsByFormFile(pResource.file.getAbsolutePath());
        // reload forms defined in file and all its scripts
        projectManager.processResource(pResource);
    }

    private void reloadScriptFile(String path) {
        mc.getScriptEngine().reloadScriptFile(path);
    }

    class FileWatcher extends Thread {
        private boolean stop = false;

        public void stopWatch() {
            this.stop = true;
        }

        @Override
        public void run() {
            this.stop = checkBaseFolder();
            try {
                Thread.sleep(DELAY_MS);
                // initialize scripts context so we can compile scripts in this thread
                Context.enter();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            while (!this.stop) {
                if (!App.getInstance().isLoading()) {
                    List<String> paths = detectFileChanges(baseFolder);
                    if (CollectionUtils.isNotEmpty(paths)) {
                        try {
                            deployChanges(paths);
                        } catch (Exception e) {
                            // pass
                        }
                    }
                }
                try {
                    Thread.sleep(TIME_BETWEEN_CHECKS_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean checkBaseFolder() {
        boolean error = false;
        if (baseFolder == null) {
            error = true;
        } else {
            File f = new File(baseFolder);
            if (!f.exists() || !f.isDirectory()) {
                error = true;
            }
        }
        if (error) {
            DevConsole.error("Invalid folder, the directory doesn't exists or is not" +
                    " a directory: " + baseFolder);
        }
        return error;
    }

    RenderingRunnable reRenderRunnable = new RenderingRunnable();

    public class RenderingRunnable implements Runnable {
        private boolean requiresControllerReload = false;

        @Override
        public void run() {
            // reload js rhino context for current view
            ViewController viewController = mc.getViewController();
            if (viewController != null) {
                String currentViewId = mc.getViewController().getId();
                mc.getScriptEngine().initScope(currentViewId);
                // render current view
                mc.renderBack(this.requiresControllerReload);
            }
        }

        public void setRequiresControllerReload(boolean requiresControllerReload) {
            this.requiresControllerReload = requiresControllerReload;
        }
    }

}
