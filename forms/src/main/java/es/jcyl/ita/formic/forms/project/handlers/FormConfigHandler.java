package es.jcyl.ita.formic.forms.project.handlers;
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

import android.net.Uri;

import org.mini2Dx.collections.CollectionUtils;

import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.elements.FormConfig;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.controllers.FormListController;
import es.jcyl.ita.formic.forms.controllers.ViewController;
import es.jcyl.ita.formic.forms.controllers.ViewControllerFactory;
import es.jcyl.ita.formic.forms.project.FormConfigRepository;
import es.jcyl.ita.formic.forms.project.ProjectResource;
import es.jcyl.ita.formic.forms.view.dag.DAGManager;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;
import static es.jcyl.ita.formic.forms.config.DevConsole.info;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FormConfigHandler extends AbstractProjectResourceHandler {
    ViewControllerFactory viewControllerFactory = ViewControllerFactory.getInstance();
    FormConfigRepository formConfigRepo;

    @Override
    public void handle(ProjectResource resource) {
        reader.addListener(this.listener);
        ConfigNode root = reader.read(Uri.fromFile(resource.file));
        FormConfig config;
        try {
            config = (FormConfig) root.getElement();
        } catch (Exception e) {
            throw new ConfigurationException(error("Invalid XML structure in file '${file}', " +
                    "does it start with <main/>?", e), e);
        }
        // register config in formConfig repo
        register(config);
        registerDags(config);
    }

    private void registerDags(FormConfig config) {
        info("Registering DAGS...");
        DAGManager dagManager = DAGManager.getInstance();
        FormListController formList = config.getList();
        if (formList != null) {
            dagManager.generateDags(formList.getView());
        }
        for (ViewController f : config.getEdits()) {
            dagManager.generateDags(f.getView());
        }
    }

    /**
     * Registers the formConfiguration in the repository and the controllers in the factory
     *
     * @param formConfig
     */
    private void register(FormConfig formConfig) {
        FormConfig existingFormConfig = formConfigRepo.findById(formConfig.getId());
        if (existingFormConfig != null) {
            throw new ConfigurationException(DevConsole.error(String.format("Duplicate id = [%s]," +
                            " the id configured in <main/> in file ${file} is already use.",
                    formConfig.getId())));
        }
        formConfigRepo.save(formConfig);

        if (formConfig.getList() != null && formConfig.isMainForm()) {
            viewControllerFactory.register(formConfig.getList());
        }
        if (CollectionUtils.isNotEmpty(formConfig.getEdits())) {
            for (ViewController edit : formConfig.getEdits()) {
                viewControllerFactory.register(edit);
            }
        }
    }

    public void setFormConfigRepo(FormConfigRepository formConfigRepo) {
        this.formConfigRepo = formConfigRepo;
    }
}
