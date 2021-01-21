package es.jcyl.ita.formic.forms.utils;
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

import java.io.File;

import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectRepository;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ProjectUtils {

    public static Project readProjectConfig(String folder) {
        return readProjectConfig(new File(folder));
    }

    public static Project readProjectConfig(File folder) {
        Config config = Config.getInstance();
        Project p = ProjectRepository.createFromFolder(folder);
        config.setCurrentProject(p);
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        return p;
    }
}
