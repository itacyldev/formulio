package util;

/*
 *
 *  * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

/**
 * Internal logger class.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {

    private static Logger logger;

    static {
        initializeLogImpl();
    }

    private static void initializeLogImpl() {
        logger = new NoOpLogger();
    }

    private static int level;

    public static void setLevel(int l) {
        level = l;
    }

    public static boolean isDebugEnabled() {
        return level <= android.util.Log.DEBUG;
    }

    public static boolean isInfoEnabled() {
        return level <= android.util.Log.INFO;
    }

    public static boolean isWarnEnabled() {
        return level <= android.util.Log.WARN;
    }

    public static void debug(String msg) {
        if (level > android.util.Log.DEBUG) {
            return;
        }
        logger.debug(msg);
    }

    public static void info(String msg) {
        if (level > android.util.Log.INFO) {
            return;
        }
        logger.info(msg);
    }

    public static void warn(String msg) {
        logger.warn(msg);
    }

    public static void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }

    public static void error(String msg) {
        logger.error(msg);
    }

    public static void error(String msg, Throwable t) {
        if (android.util.Log.WARN >= level) {
            logger.error(msg, t);
        }
    }

    public static void setLogFileName(String logsFolder, String fileName) {
        System.setProperty("FILE_NAME", fileName);
        System.setProperty("HOME_LOG", logsFolder);
        ILoggerFactory fac = LoggerFactory.getILoggerFactory();
//        if (fac != null && fac instanceof LoggerContext) {
//            LoggerContext lc = (LoggerContext) fac;
//            lc.getStatusManager().clear();
//            lc.reset();
//            lc.putProperty("FILE_NAME", fileName);
//            lc.putProperty("HOME_LOG", logsFolder);
//            ContextInitializer ci = new ContextInitializer(lc);
//            try {
//                ci.autoConfig();
//            } catch (Exception e) {
//                android.util.Log.error(e);
//            }
//        }
    }

}
