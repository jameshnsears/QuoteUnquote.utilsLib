package com.github.jameshnsears.quoteunquote.utils.preference;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.github.jameshnsears.quoteunquote.utils.BuildConfig;
import com.github.jameshnsears.quoteunquote.utils.logging.MethodLineLoggingTree;

import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class PreferenceHelper {
    @NonNull
    private final String preferenceFilename;
    @NonNull
    private final Context context;

    public PreferenceHelper(@NonNull final String preferenceFilename,
                            @NonNull final Context applicationContext) {
        this.preferenceFilename = preferenceFilename;
        this.context = applicationContext;

        if (BuildConfig.DEBUG && Timber.treeCount() == 0) {
            Timber.plant(new MethodLineLoggingTree());
        }
    }

    private static SharedPreferences.Editor getEditor(
            @NonNull final String preferencesFilename, @NonNull final Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesFilename, Context.MODE_PRIVATE);
        return sharedPreferences.edit();
    }

    public static void empty(
            @NonNull final String preferencesFilename, @NonNull final Context context) {
        final SharedPreferences.Editor editor = getEditor(preferencesFilename, context);
        editor.clear();
        editor.apply();
    }

    public static void empty(
            @NonNull final String preferenceFilename,
            @NonNull final Context applicationContext,
            final int prefix) {
        Timber.d("%d", prefix);

        final SharedPreferences.Editor editor = getEditor(preferenceFilename, applicationContext);

        final Map<String, ?> sharedPreferenceEntries = applicationContext.getSharedPreferences(preferenceFilename, Context.MODE_PRIVATE).getAll();
        for (final Map.Entry<String, ?> entry : sharedPreferenceEntries.entrySet()) {
            if (entry.getKey().startsWith(String.format(Locale.ENGLISH, "%d:", prefix))) {
                editor.remove(entry.getKey());
            }
        }

        editor.apply();
    }

    public static int countPreferences(
            @NonNull final String preferenceFilename,
            @NonNull final Context applicationContext,
            final int widgetId) {
        Timber.d("%d", widgetId);

        int preferenceCount = 0;

        final Map<String, ?> sharedPreferenceEntries = applicationContext.getSharedPreferences(preferenceFilename, Context.MODE_PRIVATE).getAll();
        for (final Map.Entry<String, ?> entry : sharedPreferenceEntries.entrySet()) {
            if (entry.getKey().startsWith(String.format(Locale.ENGLISH, "%d:", widgetId))) {
                preferenceCount++;
            }
        }

        return preferenceCount;
    }

    protected @NonNull
    SharedPreferences getPreferences() {
        return this.context.getSharedPreferences(this.preferenceFilename, Context.MODE_PRIVATE);
    }

    public @NonNull
    String getPreferenceString(@NonNull final String key) {
        return getPreferences().getString(key, "");
    }

    public int getPreferenceInt(@NonNull final String key) {
        return getPreferences().getInt(key, -1);
    }

    public boolean getPreferenceBoolean(
            @NonNull final String key, final boolean defaultValue) {
        return getPreferences().getBoolean(key, defaultValue);
    }

    public void setPreference(@NonNull final String key, final int value) {
        final SharedPreferences.Editor editor = getEditor(preferenceFilename, context);
        editor.putInt(key, value);
        editor.apply();
    }

    public void setPreference(@NonNull final String key, @NonNull final String value) {
        final SharedPreferences.Editor editor = getEditor(preferenceFilename, context);
        editor.putString(key, value);
        editor.apply();
    }

    public void setPreference(@NonNull final String key, final boolean value) {
        final SharedPreferences.Editor editor = getEditor(preferenceFilename, context);
        editor.putBoolean(key, value);
        editor.apply();
    }
}
