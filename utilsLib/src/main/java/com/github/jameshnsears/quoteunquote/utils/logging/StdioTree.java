package com.github.jameshnsears.quoteunquote.utils.logging;

import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber.DebugTree;

public class StdioTree extends DebugTree {
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        String logMessage = String.format("%s [%s] %s", timestamp, tag, message);

        System.out.println(logMessage);
    }
}
