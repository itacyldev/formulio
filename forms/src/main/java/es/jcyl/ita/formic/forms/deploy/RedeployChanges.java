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

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */

class RedeployChanges {
    private final MainController mc;
    List<String> paths;
    String repoPath;
    List<String> forms = new ArrayList<>();
    List<String> js = new ArrayList<>();
    boolean dbChanged = false;
    boolean requiresControllerReload = false;
    boolean requiresRendering = false;

    public RedeployChanges(MainController mc) {
        this.mc = mc;
    }

    public void classify(List<String> paths) {
        clear();
        this.paths = paths;
        for (String str : paths) {
            if (str.endsWith("repos.xml")) {
                this.repoPath = str;
//            } else if (str.contains("data/") && str.endsWith(".sqlite")) {
//                this.dbChanged = true;
            } else if (str.contains("forms/") && str.endsWith(".xml")) {
                this.forms.add(str);
            }
            if (str.endsWith(".js")) {
                this.js.add(str);
            }
        }
        checkRenderingRequirement();
    }

    private void checkRenderingRequirement() {
        if (this.repoPath != null || this.dbChanged) {
            // repo definition has changed
            this.requiresRendering = true;
            this.requiresControllerReload = true;
            return;
        }
        // check if the xml file definition of current view has changed
        FormConfig currentForm = mc.getViewController().getFormConfig();
        String configFile = FilenameUtils.getName(currentForm.getFilePath());
        for (String formPath : this.forms) {
            if (formPath.toLowerCase().endsWith(configFile.toLowerCase())) {
                this.requiresRendering = true;
                this.requiresControllerReload = true;
                return;
            }
        }
        // check if the js file changes affects current view
        Set<String> formIds = new HashSet<>();
        for (String jsFile : this.js) {
            formIds.addAll(mc.getScriptEngine().findDependantForms(jsFile));
        }
        String currentViewId = mc.getViewController().getId();
        if (formIds.contains(currentViewId)) {
            this.requiresRendering = true;
        }
    }

    public boolean isRepoChanged() {
        return this.repoPath != null;
    }

    public boolean isDbChanged() {
        return dbChanged;
    }

    public boolean isFormsChanged() {
        return this.forms.size() != 0;
    }

    public boolean isJsChanged() {
        return this.js.size() != 0;
    }

    public void clear() {
        this.requiresRendering = false;
        this.requiresControllerReload = false;
        this.dbChanged = false;
        this.repoPath = null;
        this.forms.clear();
        this.js.clear();
    }
}