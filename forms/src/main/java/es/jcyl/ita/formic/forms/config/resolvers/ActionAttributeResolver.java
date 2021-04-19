package es.jcyl.ita.formic.forms.config.resolvers;
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

import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.config.reader.ReadingProcessListener;
import es.jcyl.ita.formic.forms.project.Project;

/**
 * Responsible for the asignation of actions to elements....COMPLETE THIS
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ActionAttributeResolver extends AbstractAttributeResolver implements ReadingProcessListener {



    @Override
    public Object resolve(ConfigNode node, String attName) {
        return null;
    }

    /*********************/
    /** ReadingProcessListener Interface */
    /*********************/

    @Override
    public void fileStart(String currentFile) {

    }

    @Override
    public void fileEnd(String currentFile) {

    }

    @Override
    public void viewStart() {

    }

    @Override
    public void viewEnd() {

    }

    @Override
    public void elementStart(String tag) {

    }

    @Override
    public void elementEnd(String tag) {

    }

    @Override
    public Project getProject() {
        return null;
    }

    @Override
    public void setProject(Project project) {

    }

    @Override
    public String getCurrentFile() {
        return null;
    }
}
