package es.jcyl.ita.frmdrd.ui.components.media;
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

import es.jcyl.ita.frmdrd.config.Config;

/**
 * Helper class to locate resources with paths relative to current project
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class MediaResourceLocator {

    public static File locate(String path) {
        return new File(Config.getInstance().getCurrentProject().getBaseFolder(), path);
    }

    public static File locateImage(String path) {
        File file = new File(path);
        if (!file.isAbsolute()) {
            return file;
        }
        return new File(getFolderByType("pictures"), path);
    }

    public static String relativeImagePath(String absFileName) {
        return relativePath(absFileName, "pictures");
    }

    public static String relativePath(String absFileName, String type) {
        String base = getFolderByType(type);
        int rootLength = base.length();
        final String relFileName = absFileName.substring(rootLength + 1);
        return relFileName;
    }

    private static String getFolderByType(String type) {
        return Config.getInstance().getCurrentProject().getFolderByType(type);
    }


}
