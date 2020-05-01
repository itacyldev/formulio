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

import es.jcyl.ita.frmdrd.config.parser.DummyFormConfigParser;
import es.jcyl.ita.frmdrd.config.parser.FormConfigParser;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ConfigFacade {

    private static boolean configLoaded = false;

    public void init() {
        if (!configLoaded) {
            RepositoryProjectConfReader configReader = new RepositoryProjectConfReader();
            configReader.read();

            FormConfigParser parser = new DummyFormConfigParser();
            parser.parseFormConfig("");

            // customize data type converters
            ConfigConverters confConverter = new ConfigConverters();
            confConverter.init();

            configLoaded = true;
        }
    }
}
