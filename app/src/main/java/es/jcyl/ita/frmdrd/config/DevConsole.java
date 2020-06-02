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

import org.apache.commons.jexl3.JexlContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.config.reader.ConfigReadingInfo;
import es.jcyl.ita.frmdrd.el.JexlUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * Gathers error to show them in developer console and provides decorartors to enrich the messages
 * with context information ${tag}, ${file} and ${line}
 */
public class DevConsole {
    private static final String DEV_CONSOLE = "devconsole";
    private static int level = Log.DEBUG;

    private static ConfigReadingInfo configReadingInfo;
    // TODO: limit this with a pile
    private static List<String> console = new ArrayList<>();


    public static void setLevel(int l) {
        level = l;
    }

    public static void clear() {
        console.clear();
    }

    public static String error(String msg) {
        // TODO: link log library
        return _writeMsg(Log.ERROR, msg, null);
    }

    public static String info(String s) {
        return _writeMsg(Log.INFO, s, null);
    }

    public static String error(String msg, Throwable t) {
        // TODO: link log library
        return _writeMsg(Log.ERROR, msg, t);
    }


    public static String warn(String msg) {
        // TODO: link log library
        return _writeMsg(Log.WARN, msg, null);
    }

    //
    private static String _writeMsg(int errorLevel, String msg, Throwable t) {
        if (errorLevel < level) {
            return msg;
        }
        String effMsg = String.valueOf(JexlUtils.eval(devContext, msg));
        console.add(effMsg);
        if (errorLevel == Log.ERROR) {
            System.err.println(effMsg);
        } else {
            System.out.println(effMsg);
        }
//        Log.e(DEV_CONSOLE, effMsg);
        if (t != null) {
            Log.e(DEV_CONSOLE, Log.getStackTraceString(t));
        }
        return effMsg;
    }


    public static void debug(String s) {
        _writeMsg(Log.DEBUG, s, null);
    }

    public static void setConfigReadingInfo(ConfigReadingInfo info) {
        configReadingInfo = info;
    }

    private static final Set<String> PROPS = new HashSet<String>(Arrays.asList("project", "file", "line", "tag"));
    private static JexlContext devContext = new JexlContext() {
        @Override
        public Object get(String name) {
            if (configReadingInfo == null) {
                return null;
            }
            if (name.equals("line")) {
                return (configReadingInfo.getXpp() != null) ? configReadingInfo.getXpp().getLineNumber() : -1;
            } else if (name.equals("project")) {
                return configReadingInfo.getProject().getId();
            } else if (name.equals("projectFolder")) {
                return configReadingInfo.getProject().getBaseFolder();
            } else if (name.equals("tag")) {
                return configReadingInfo.getCurrentTag();
            } else if (name.equals("file")) {
                return configReadingInfo.getCurrentFile();
            }
            return "";
        }

        @Override
        public void set(String name, Object value) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public boolean has(String name) {
            return PROPS.contains(name);
        }
    };


    public static List<String> getMessages() {
        return console;
    }

    public static void debug(ConfigNode root) {
        // TODO #204330
    }

    public static boolean isDebugEnabled() {
        return isLevelEnabled(Log.DEBUG);
    }

    public static boolean isInfoEnabled() {
        return isLevelEnabled(Log.DEBUG);
    }

    public static boolean isWarnEnabled() {
        return isLevelEnabled(Log.DEBUG);
    }

    private static boolean isLevelEnabled(int logLevel) {
        return level > logLevel;
    }

}
