package es.jcyl.ita.formic.forms.config;
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

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.config.reader.ConfigReadingInfo;
import es.jcyl.ita.formic.forms.el.JexlUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * Gathers error to show them in developer console and provides decorartors to enrich the messages
 * with context information ${tag}, ${file} and ${line}
 */
public class DevConsole {
    private static final String DEV_CONSOLE = "devconsole";
    private static int level = Log.DEBUG;

    private static ConfigReadingInfo configReadingInfo;

    public static final int COLOR_ERROR = Color.RED;
    public static final int COLOR_INFO = Color.GREEN;
    public static final int COLOR_WARN = Color.YELLOW;
    public static final int COLOR_DEBUG = Color.BLUE;

    private static final int MAX_SIZE = 300;
    private static Queue<SpannableString> console = new LinkedList<SpannableString>();

    public static void clear() {
        console.clear();
    }

    private static void add(String effMsg, int level, int color) {

        String msg = StringUtils.join(String.format("%1s [%2s]:%3s", getDateTimeStamp(), formatLevel(level),  effMsg), "\n");

        SpannableString redSpannable= new SpannableString(msg);
        redSpannable.setSpan(new ForegroundColorSpan(color), 0, msg.length(), 0);

        console.add(redSpannable);
        if (console.size() > MAX_SIZE){
            console.remove();
        }
    }

    private static String getDateTimeStamp(){
        Date dateNow = Calendar.getInstance().getTime();
        return (String) ConvertUtils.convert(dateNow, String.class);
    }

    private static String formatLevel(int level)  {
        String strLevel = "";
        try {
            Class<Log> c = Log.class;
            for (Field f : Log.class.getDeclaredFields()) {
                if (f.getInt(c) == level) {
                    strLevel = f.getName();
                    break;
                }
            }
        }catch (IllegalAccessException e){}

        return strLevel;
    }


    public static String error(String msg) {
        // TODO: link log library
        return _writeMsg(Log.ERROR, COLOR_ERROR, msg, null);
    }

    public static String info(String s) {
        return _writeMsg(Log.INFO, COLOR_INFO, s, null);
    }

    public static String error(String msg, Throwable t) {
        // TODO: link log library
        return _writeMsg(Log.ERROR, COLOR_ERROR, msg, t);
    }


    public static String warn(String msg) {
        // TODO: link log library
        return _writeMsg(Log.WARN, COLOR_WARN, msg, null);
    }

    public static void debug(String s) {
        _writeMsg(Log.DEBUG, COLOR_DEBUG, s, null);
    }

    //
    private static String _writeMsg(int errorLevel, int color, String msg, Throwable t) {
        if (errorLevel < level) {
            return msg;
        }
        String effMsg = String.valueOf(JexlUtils.eval(devContext, msg));
        add(effMsg, errorLevel, color);

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

    public static void setConfigReadingInfo(ConfigReadingInfo info) {
        configReadingInfo = info;
    }

    public static void setLevel(int l) {
        level = l;
    }

    public static Queue<SpannableString> getMessages() {
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
        return logLevel >= level;
    }

}
