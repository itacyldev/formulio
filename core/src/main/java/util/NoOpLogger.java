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

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Dummy implementor of org.slf4j.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class NoOpLogger implements Logger {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String msg) {

    }

    @Override
    public void trace(String format, Object arg) {

    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        System.out.println(String.format(format, arg1, arg2));
    }

    @Override
    public void trace(String format, Object... arguments) {
        System.out.println(String.format(format,arguments));
    }

    @Override
    public void trace(String msg, Throwable t) {
        System.out.println(msg);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override
    public void trace(Marker marker, String msg) {
        System.out.println(msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {

    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {

    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {

    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String msg) {
        System.out.println(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        System.out.println(String.format(format, arg));
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        System.out.println(String.format(format, arg1, arg2));
        android.util.Log.d("jobs", String.format(format, arg1, arg2));
    }

    @Override
    public void debug(String format, Object... arguments) {
        System.out.println(String.format(format, arguments));
    }

    @Override
    public void debug(String msg, Throwable t) {

    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return false;
    }

    @Override
    public void debug(Marker marker, String msg) {

    }

    @Override
    public void debug(Marker marker, String format, Object arg) {

    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {

    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {

    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(String msg) {
        android.util.Log.i("jobs", msg);
    }

    @Override
    public void info(String format, Object arg) {
        android.util.Log.i("jobs", String.format(format, arg));
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        android.util.Log.i("jobs", String.format(format, arg1, arg2));
    }

    @Override
    public void info(String format, Object... arguments) {
        android.util.Log.i("jobs", String.format(format, arguments));
    }

    @Override
    public void info(String msg, Throwable t) {
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return false;
    }

    @Override
    public void info(Marker marker, String msg) {

    }

    @Override
    public void info(Marker marker, String format, Object arg) {

    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {

    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {

    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(String msg) {

    }

    @Override
    public void warn(String format, Object arg) {

    }

    @Override
    public void warn(String format, Object... arguments) {

    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {

    }

    @Override
    public void warn(String msg, Throwable t) {

    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return false;
    }

    @Override
    public void warn(Marker marker, String msg) {

    }

    @Override
    public void warn(Marker marker, String format, Object arg) {

    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {

    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {

    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(String msg) {
        android.util.Log.e("jobs",msg);
    }

    @Override
    public void error(String format, Object arg) {
        android.util.Log.e("jobs",String.format(format, arg));
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        android.util.Log.e("jobs",String.format(format, arg1, arg2));
    }

    @Override
    public void error(String format, Object... arguments) {
        android.util.Log.e("jobs", String.format(format, arguments));
    }

    @Override
    public void error(String msg, Throwable t) {
        android.util.Log.e("jobs", msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return false;
    }

    @Override
    public void error(Marker marker, String msg) {

    }

    @Override
    public void error(Marker marker, String format, Object arg) {

    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {

    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {

    }
}
