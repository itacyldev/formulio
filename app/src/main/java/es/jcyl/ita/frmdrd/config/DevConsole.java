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

import org.apache.commons.jexl3.MapContext;
import org.xmlpull.v1.XmlPullParser;

import es.jcyl.ita.frmdrd.el.JexlUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * Gathers error to show them in developer console and provides decorartors to enrich the messages
 * with context information ${tag}, ${file} and ${line}
 */
public class DevConsole {

    private static XmlPullParser xpp;
    private static boolean hasError;
    private static MapContext ctx;
    private static String currentFile;

    public static void clear() {
        ctx.clear();
    }

    public static String error(String msg) {
        // TODO: link log library
        hasError = true;
        return _writeMsg(Log.ERROR, msg, null);
    }

    public static String error(String msg, Throwable t) {
        // TODO: link log library
        hasError = true;
        return _writeMsg(Log.ERROR, msg, t);
    }


    public static String warn(String msg) {
        // TODO: link log library
        return _writeMsg(Log.WARN, msg, null);
    }

    //
    private static String _writeMsg(int errorLevel, String msg, Throwable t) {
        if(xpp!=null){
            ctx.set("line", xpp.getLineNumber());
        }
        String effMsg = String.valueOf(JexlUtils.eval(ctx, msg));
        System.out.println(effMsg);
        if (t != null) {
            System.err.println(t.getStackTrace());
        }
        return effMsg;
    }

    public static void setCurrentFile(String filePath) {
        ctx = new MapContext();
        ctx.set("file", filePath);
        currentFile = filePath;
        hasError = false;
    }

    public static boolean hasCurrentFileError() {
        return hasError;
    }

    public static void setCurrentElement(String tag) {
        if(ctx == null){
            ctx = new MapContext();
        }
        ctx.set("tag", tag);
    }

    public static void setParser(XmlPullParser parser) {
        xpp = parser;
    }

    public static void debug(String s) {
        _writeMsg(Log.DEBUG, s, null);
    }
}
