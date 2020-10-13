package es.jcyl.ita.formic.repo.config.reader;
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

import org.xmlpull.v1.XmlPullParser;

import es.jcyl.ita.formic.forms.project.Project;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 *
 * Holder to share information during the configuration process.
 */
public class ConfigReadingInfo implements ReadingProcessListener {
    private Project project;
    private String currentTag;
    private String currentFile;
    private static XmlPullParser xpp;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(String currentFile) {
        this.currentFile = currentFile;
    }

    public static XmlPullParser getXpp() {
        return xpp;
    }

    public static void setXpp(XmlPullParser xpp) {
        ConfigReadingInfo.xpp = xpp;
    }

    public String getCurrentTag() {
        return this.currentTag;
    }


    @Override
    public void newElement(String tag) {
        this.currentTag = tag;
    }
}
