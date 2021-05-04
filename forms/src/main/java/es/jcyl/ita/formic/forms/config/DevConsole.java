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

import android.util.Log;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.ConvertUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.config.reader.ConfigReadingInfo;
import es.jcyl.ita.formic.forms.el.JexlFormUtils;


/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * Gathers error to show them in developer console and provides decorartors to enrich the messages
 * with context information ${tag}, ${file} and ${line}
 */
public class DevConsole {

    private static final String DEV_CONSOLE = "devconsole";
    private static int level;

    private static ConfigReadingInfo configReadingInfo;

    private static final int MAX_SIZE = 300;
    //priority level: Debug > Info > Warn > Error
    private static Queue<String> consoleDebug = new LinkedList<String>();
    private static Queue<String> consoleInfo = new LinkedList<String>();

    private static Queue<String> consoleWarn = new LinkedList<String>();
    private static Queue<String> consoleError = new LinkedList<String>();

    private static final Logger logger = LoggerFactory.getLogger(DevConsole.class);

    public static void clear() {
        clearDebug();
        clearInfo();
        clearWarn();
        clearError();
    }

    private static void clearDebug() {
        consoleDebug.clear();
    }

    private static void clearInfo() {
        consoleInfo.clear();
    }

    private static void clearWarn() {
        consoleWarn.clear();
    }

    private static void clearError() {
        consoleError.clear();
    }

    private static void addError(String msg) {
        add(msg, consoleError);
        addWarn(msg);
    }

    private static void addWarn(String msg) {
        add(msg, consoleWarn);
        addInfo(msg);
    }

    private static void addInfo(String msg) {
        add(msg, consoleInfo);
        addDebug(msg);
    }

    private static void addDebug(String msg) {
        add(msg, consoleDebug);
    }

    private static void add(String msg, Queue<String> console) {
        console.add(msg);
        if (console.size() > MAX_SIZE) {
            console.remove();
        }
    }

    private static String addTimeStamp(String effMsg, int level) {
        String strLevel = getStrLevel(level);
        String msg = StringUtils.join(String.format("%1s [%2s]:%3s", getDateTimeStamp(), strLevel, effMsg), "\n");

        return msg;
    }

    private static String getDateTimeStamp() {
        Date dateNow = Calendar.getInstance().getTime();
        return (String) ConvertUtils.convert(dateNow, String.class);
    }

    public static String getStrLevel(int level) {
        String strLevel = "";
        switch (level) {
            case 3:
                strLevel = "DEBUG";
                break;
            case 4:
                strLevel = "INFO";
                break;
            case 5:
                strLevel = "WARN";
                break;
            default:
                strLevel = "ERROR";
                break;
        }

        return strLevel;
    }


    public static String error(String msg) {
        // TODO: link log library
        String effMsg = getMsg(Log.ERROR, msg, null);
        if (Log.ERROR >= level) {
            addError(addTimeStamp(effMsg, Log.ERROR));
            logger.error(effMsg);
        }
        return effMsg;
    }

    public static String info(String s) {
        String effMsg = getMsg(Log.INFO, s, null);
        if (Log.INFO >= level) {
            addInfo(addTimeStamp(effMsg, Log.INFO));
            logger.info(effMsg);
        }
        return effMsg;
    }

    public static String error(String msg, Throwable t) {
        // TODO: link log library
        String effMsg = getMsg(Log.ERROR, msg, t);
        if (Log.ERROR >= level) {
//            addError(getSpannableString(effMsg, Log.ERROR, COLOR_ERROR));
            logger.error(msg, t);
        }
        return effMsg;
    }


    public static String warn(String msg) {
        // TODO: link log library
        String effMsg = getMsg(Log.WARN, msg, null);
        if (Log.WARN >= level) {
            addWarn(addTimeStamp(effMsg, Log.WARN));
            logger.warn(msg);
        }
        return effMsg;
    }

    public static String debug(String msg) {
        String effMsg = getMsg(Log.DEBUG, msg, null);
        if (Log.DEBUG >= level) {
            addDebug(addTimeStamp(effMsg, Log.DEBUG));
            logger.debug(msg);
        }
        return effMsg;
    }

    //
    private static String getMsg(int errorLevel, String msg, Throwable t) {
        if (StringUtils.isBlank(msg)) {
            return "";
        }
        if (errorLevel < level) {
            return msg;
        }
        String effMsg = String.valueOf(JexlFormUtils.eval(devContext, msg));

        if (errorLevel == Log.ERROR) {
            System.err.println(effMsg);
        } else {
            System.out.println(effMsg);
        }
        if (t != null) {
            System.err.println(t.getMessage());
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

    public static Queue<String> getMessages(String filterBy, int logLevel) {
        Queue<String> console = null;
        switch (logLevel) {
            case Log.ERROR: {
                console = consoleError;
                break;
            }
            case Log.WARN: {
                console = consoleWarn;
                break;
            }
            case Log.INFO: {
                console = consoleInfo;
                break;
            }
            case Log.DEBUG: {
                console = consoleDebug;
                break;
            }
            default: {
                console = consoleDebug;
                break;
            }
        }
        return filter(console, filterBy);
    }

    private static Queue<String> filter(Queue<String> console, String filterBy) {
        Queue<String> filteredConsole = new LinkedList<String>();
        if (StringUtils.isNotBlank(filterBy)) {
            for (String msg : console) {
                if (StringUtils.containsIgnoreCase(msg, filterBy)) {
                    add(msg, filteredConsole);
                }
            }
        } else {
            filteredConsole.addAll(console);
        }
        return filteredConsole;
    }

    public static void setLogFileName(String projectsFolder, String fileName) {

        String logFolder = projectsFolder + "/" + fileName + "/logs";
        System.setProperty("FILE_NAME", fileName);
        System.setProperty("HOME_LOG", logFolder);
        ILoggerFactory fac = LoggerFactory.getILoggerFactory();
        if (fac != null && fac instanceof LoggerContext) {
            LoggerContext lc = (LoggerContext) fac;
            lc.getStatusManager().clear();
            lc.reset();
            lc.putProperty("FILE_NAME", fileName);
            lc.putProperty("HOME_LOG", logFolder);
            ContextInitializer ci = new ContextInitializer(lc);
            try {
                ci.autoConfig();
            } catch (JoranException e) {
                e.printStackTrace();
            }
        }
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

    public static int getLevel() {
        return level;
    }

    /***********/
    /**
     * Instance accesible methods to give access to console in js scripts
     * /
     ***********/
    public void log(String msg) {
        DevConsole.info(msg);
    }

    public void err(String msg) {
        DevConsole.error(msg);
    }


}
