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

import es.jcyl.ita.frmdrd.config.reader.XMLFormConfigReader;
import es.jcyl.ita.frmdrd.config.reader.dummy.DummyFormConfigReader;
import es.jcyl.ita.frmdrd.config.repo.RepositoryConfReader;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Configuration initializer and commons point to store and share configuration parameters.
 */
public class Config {

    private static boolean configLoaded = false;
    private String projectFolder;
    private static RepositoryConfReader repoConfigReader;
    private static XMLFormConfigReader formConfigReader;


    public Config(String projectFolder) {
        this.projectFolder = projectFolder;
    }


    public void init() {
        if (!configLoaded) {
            repoConfigReader = new RepositoryConfReader(this.projectFolder);

            // customize data type converters
            ConfigConverters confConverter = new ConfigConverters();
            confConverter.init();

            configLoaded = true;
        }
    }

    public void read() {
        repoConfigReader.read();
        DummyFormConfigReader reader = new DummyFormConfigReader();
        reader.read(null);
    }

    public static RepositoryConfReader getRepoConfigReader() {
        return repoConfigReader;
    }

    public static XMLFormConfigReader getFormConfigReader() {
        return formConfigReader;
    }
}
