package com.novoda.staticanalysis

import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.slf4j.Marker

class QuietLogger implements Logger {

    public static final QuietLogger INSTANCE = new QuietLogger()

    private QuietLogger() {
        // no instance
    }

    @Override
    public String getName() {
        return "QuietLogger";
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
    }

    @Override
    public void trace(String format, Object... arguments) {
    }

    @Override
    public void trace(String msg, Throwable t) {
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override
    public void trace(Marker marker, String msg) {
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
    }

    @Override
    public void debug(String format, Object arg) {
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
    }

    @Override
    public boolean isLifecycleEnabled() {
        return false;
    }

    @Override
    public void debug(String format, Object... arguments) {
    }

    @Override
    public void lifecycle(String message) {
    }

    @Override
    public void lifecycle(String message, Object... objects) {
    }

    @Override
    public void lifecycle(String message, Throwable throwable) {
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
    }

    @Override
    public void info(String format, Object arg) {
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
    }

    @Override
    public void info(String format, Object... arguments) {
    }

    @Override
    public boolean isQuietEnabled() {
        return false;
    }

    @Override
    public void quiet(String message) {
    }

    @Override
    public void quiet(String message, Object... objects) {
    }

    @Override
    public void quiet(String message, Throwable throwable) {
    }

    @Override
    public boolean isEnabled(LogLevel level) {
        return false;
    }

    @Override
    public void log(LogLevel level, String message) {
    }

    @Override
    public void log(LogLevel level, String message, Object... objects) {
    }

    @Override
    public void log(LogLevel level, String message, Throwable throwable) {
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
        return false;
    }

    @Override
    public void error(String msg) {
    }

    @Override
    public void error(String format, Object arg) {
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
    }

    @Override
    public void error(String format, Object... arguments) {
    }

    @Override
    public void error(String msg, Throwable t) {
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
