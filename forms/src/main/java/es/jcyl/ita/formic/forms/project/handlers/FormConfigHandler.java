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
import es.jcyl.ita.formic.forms.controllers.FormController;
import es.jcyl.ita.formic.forms.controllers.FormControllerFactory;
import es.jcyl.ita.formic.forms.controllers.FormEditController;
import es.jcyl.ita.formic.forms.project.FormConfigRepository;
import es.jcyl.ita.formic.forms.project.ProjectResource;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FormConfigHandler extends AbstractProjectResourceHandler {
    FormControllerFactory formFactory = FormControllerFactory.getInstance();
    FormConfigRepository formConfigRepo;


    @Override
    public void handle(ProjectResource resource) {
        reader.setListener(this.listener);
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
    }

    private void register(FormController form) {
        formFactory.register(form);
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
                    " the id configured in <main/> in file ${file} is already used in the " +
                    "file: [%s].", formConfig.getId(), existingFormConfig.getFilePath())));
        }
        formConfigRepo.save(formConfig);

        register(formConfig.getList());
        if (CollectionUtils.isNotEmpty(formConfig.getEdits())) {
            for (FormEditController edit : formConfig.getEdits()) {
                register(edit);
            }
        }
    }

    public void setFormConfigRepo(FormConfigRepository formConfigRepo) {
        this.formConfigRepo = formConfigRepo;
    }
}
