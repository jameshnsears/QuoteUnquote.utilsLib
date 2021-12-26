package com.github.jameshnsears.quoteunquote.utils.audit;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.concurrent.ConcurrentMap;

import timber.log.Timber;

public final class AuditEventHelper {
    public static synchronized void createInstance(@NonNull final Application application) {
        Timber.d("deliberately not implemented");
    }

    public static void auditEvent(@NonNull final String auditEvent,
                                  @NonNull final ConcurrentMap<String, String> properties) {
        Timber.d("deliberately not implemented");
    }
}
