package com.github.jameshnsears.quoteunquote.utils.audit;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.github.jameshnsears.quoteunquote.utils.BuildConfig;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.Flags;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import timber.log.Timber;

public final class AuditEventHelper {
    private static AuditEventHelper auditEventHelperSingleton;
    private static FirebaseAnalytics firebaseAnalytics;

    private AuditEventHelper(final Application application) {
        AppCenter.start(application, BuildConfig.APPCENTER_KEY, Analytics.class, Crashes.class);
        firebaseAnalytics = FirebaseAnalytics.getInstance(application.getApplicationContext());
    }

    public static synchronized void createInstance(@NonNull final Application application) {
        if (auditEventHelperSingleton == null) {
            auditEventHelperSingleton = new AuditEventHelper(application);
        }
    }

    public static synchronized void auditEvent(@NonNull final String auditEvent, @NonNull final ConcurrentMap<String, String> properties) {
        // adb shell setprop debug.firebase.analytics.app com.github.jameshnsears.quoteunquote
        // adb shell setprop debug.firebase.analytics.app .none.
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            // https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics#logEvent(java.lang.String,%20android.os.Bundle)
            String key = entry.getKey();
            if (key.length() > 40) {
                key = key.substring(0, 40);
            }

            String value = entry.getValue();
            if (value.length() > 100) {
                value = value.substring(0, 100);
            }

            Timber.d("%s=%s", key, value);

            bundle.putString(key, value);
        }

        if (firebaseAnalytics != null) {
            firebaseAnalytics.logEvent(auditEvent, bundle);
        }

        Analytics.trackEvent(auditEvent, properties, Flags.NORMAL);
    }
}
