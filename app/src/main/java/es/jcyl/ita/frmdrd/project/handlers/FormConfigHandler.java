package es.jcyl.ita.frmdrd.project.handlers;
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

import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.FormConfig;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.config.reader.xml.XmlConfigFileReader;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.forms.FormControllerFactory;
import es.jcyl.ita.frmdrd.forms.FormEditController;
import es.jcyl.ita.frmdrd.project.FormConfigRepository;
import es.jcyl.ita.frmdrd.project.ProjectResource;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FormConfigHandler extends AbstractProjectResourceHandler<FormConfig> {
    FormControllerFactory formFactory = FormControllerFactory.getInstance();
    FormConfigRepository formConfigRepo;


    @Override
    public FormConfig handle(ProjectResource resource) {
        XmlConfigFileReader reader = new XmlConfigFileReader();
        reader.setListener(this.listener);
        ConfigNode root = reader.read(Uri.fromFile(resource.file));
        FormConfig config;
        try {
            config = (FormConfig) root.getElement();
        } catch (Exception e) {
            throw new ConfigurationException(error("Invalid XML structure on file '${file}', " +
                    "does it start with <main/>?", e), e);
        }
        // register config in formConfig repo
        register(config);
        return config;
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
