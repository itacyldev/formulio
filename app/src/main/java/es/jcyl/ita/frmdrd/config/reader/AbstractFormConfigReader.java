package es.jcyl.ita.frmdrd.config.reader;

import org.mini2Dx.collections.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.FormConfig;
import es.jcyl.ita.frmdrd.forms.FormController;
import es.jcyl.ita.frmdrd.forms.FormControllerFactory;
import es.jcyl.ita.frmdrd.forms.FormEditController;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public abstract class AbstractFormConfigReader {
    FormControllerFactory controllerFactory = FormControllerFactory.getInstance();


    public FormConfig read(File file) throws ConfigurationException {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ConfigurationException(error("Error while trying to read configuration file: " + file.getAbsolutePath()));
        }
        return read(file.getAbsolutePath(), is);
    }

    public FormConfig read(String name, File file) throws ConfigurationException {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ConfigurationException(error("Error while trying to read configuration file: " + file.getAbsolutePath()));
        }
        return read(name, is);
    }

    public abstract FormConfig read(String name, InputStream is) throws ConfigurationException;


    protected void register(FormController form) {
        controllerFactory.register(form);
    }

    protected void register(FormConfig formConfig) {
        controllerFactory.register(formConfig.getList());
        if (CollectionUtils.isNotEmpty(formConfig.getEdits())) {
            for (FormEditController edit : formConfig.getEdits()) {
                register(edit);
            }
        }
    }
}
