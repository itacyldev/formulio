package es.jcyl.ita.frmdrd.config.resolvers;
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

import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Converts the relative paths given in files
 */
public class RelativePathAttResolver extends AbstractAttributeResolver<String> {

    public RelativePathAttResolver() {

    }

    @Override
    public String resolve(ConfigNode node, String attName) {
        String path = node.getAttribute(attName);
        File referencedFile = new File(path);

        if (!referencedFile.isAbsolute()) {
            // config relative path to project data folder
            String dataBasePath = factory.getInfo().getProject().getDataFolder();
            referencedFile = new File(dataBasePath, path);
        }
        if (!referencedFile.exists()) {
            throw new ConfigurationException(error(String.format("The referenced file '%s' in element %s ' inside file " +
                    "'${file}' does not exists.", referencedFile.getAbsolutePath(), node.toString())));
        }
        return referencedFile.getAbsolutePath();
    }
}
