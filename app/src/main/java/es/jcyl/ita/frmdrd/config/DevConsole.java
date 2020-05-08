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

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

import java.util.logging.Level;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class DevConsole {

    private static String currentFile;
    private static String currentTag;
    private static XmlPullParser xpp;
    private static boolean hasError;

    public static String error(String msg) {
        // TODO: link log library
        hasError = true;
        _writeMsg(Log.ERROR, msg);
        return msg;
    }

    public static String error(String msg, Throwable t) {
        // TODO: link log library
        hasError = true;
        _writeMsg(Log.ERROR, msg);
        return msg;
    }


    public static void warn(String msg) {
        // TODO: link log library
        _writeMsg(Log.WARN, msg);
    }

    //
    private static void _writeMsg(int errorLevel, String msg) {
        System.out.println(msg);
    }

    public static void setCurrentFile(String filePath) {
        currentFile = filePath;hasError = false;
    }
    public static boolean  hasCurrentFileError(){
        return hasError;
    }

    public static void setCurrentElement(String tag) {
        currentTag = tag;
    }

    public static void setParser(XmlPullParser xpp) {
        xpp = xpp;
    }
}
