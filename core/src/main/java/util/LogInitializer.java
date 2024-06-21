package util;
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

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * Log4j configuration for Android
 */
public class LogInitializer {

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
            } catch (Exception e) {
                Log.e("Error", "Error while setting log file", e);
            }
        }
    }
}