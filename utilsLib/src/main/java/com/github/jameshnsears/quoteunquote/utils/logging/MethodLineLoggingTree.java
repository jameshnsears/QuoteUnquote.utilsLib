package com.github.jameshnsears.quoteunquote.utils.logging;

import androidx.annotation.NonNull;

import timber.log.Timber.DebugTree;

public final class MethodLineLoggingTree extends DebugTree {
    @Override
    protected @NonNull
    String createStackElementTag(@NonNull final StackTraceElement element) {
        return String.format(
                "%s.%s, %s",
                super.createStackElementTag(element),
                element.getMethodName(),
                element.getLineNumber());
    }
}
