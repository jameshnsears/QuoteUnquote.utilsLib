package com.github.jameshnsears.quoteunquote.utils.preference

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class PreferenceHelperTest {
    private val preferenceFilename = "preferenceFilename"
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var applicationContext: Context

    @Before
    fun init() {
        applicationContext = getApplicationContext()
        preferenceHelper = PreferenceHelper(preferenceFilename, applicationContext)
    }

    @Test
    fun `confirm api works as expected`() {
        assertThat(
            PreferenceHelper.countPreferences(
                preferenceFilename,
                applicationContext,
                1,
            ),
            equalTo(0),
        )

        preferenceHelper.setPreference("1:int", 234)
        assertThat(preferenceHelper.getPreferenceInt("1:int"), equalTo(234))

        preferenceHelper.setPreference("2:boolean", true)
        assertThat(preferenceHelper.getPreferenceBoolean("2:boolean", true), `is`(true))
        assertThat(preferenceHelper.getPreferenceBoolean("boolean-default", false), `is`(false))

        preferenceHelper.setPreference("3:string", "abc")
        assertThat(preferenceHelper.getPreferenceString("3:string"), equalTo("abc"))

        PreferenceHelper.empty(preferenceFilename, applicationContext, 1)
        assertThat(
            PreferenceHelper.countPreferences(
                preferenceFilename,
                applicationContext,
                1,
            ),
            equalTo(0),
        )
        assertThat(preferenceHelper.getPreferenceInt("1:int"), equalTo(-1))

        PreferenceHelper.empty(preferenceFilename, applicationContext)
        assertThat(preferenceHelper.getPreferenceString("3:string"), equalTo(""))

        assertThat(
            PreferenceHelper.countPreferences(preferenceFilename, applicationContext, 3),
            equalTo(0),
        )
    }
}
