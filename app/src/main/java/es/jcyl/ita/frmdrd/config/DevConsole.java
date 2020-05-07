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

import org.xmlpull.v1.XmlPullParser;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class DevConsole {

    private static String currentFile;
    private static String currentTag;
    private static XmlPullParser xpp;

    public static String error(String msg) {
        // TODO: link log library
        return msg;
    }

    public static String error(String msg, Throwable t) {
        // TODO: link log library
        return msg;
    }

    public static void warn(String msg) {
        // TODO: link log library
    }

    //

    public static void setCurrentFile(String filePath) {
        currentFile = filePath;
    }

    public static void setCurrentElement(String tag) {
        currentTag = tag;
    }

    public static void setParser(XmlPullParser xpp) {
        xpp = xpp;
    }
}
