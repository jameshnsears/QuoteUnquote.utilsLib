package com.github.jameshnsears.quoteunquote.utils.preference

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
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
        assertTrue(PreferenceHelper.countPreferences(preferenceFilename, applicationContext, 1) == 0)

        preferenceHelper.setPreference("1:int", 234)
        assertTrue(preferenceHelper.getPreferenceInt("1:int") == 234)

        preferenceHelper.setPreference("2:boolean", true)
        assertTrue(preferenceHelper.getPreferenceBoolean("2:boolean", true))
        assertFalse(preferenceHelper.getPreferenceBoolean("boolean-default", false))

        preferenceHelper.setPreference("3:string", "abc")
        assertTrue(preferenceHelper.getPreferenceString("3:string") == "abc")

        PreferenceHelper.empty(preferenceFilename, applicationContext, 1)
        assertTrue(PreferenceHelper.countPreferences(preferenceFilename, applicationContext, 1) == 0)
        assertTrue(preferenceHelper.getPreferenceInt("1:int") == -1)

        PreferenceHelper.empty(preferenceFilename, applicationContext)
        assertTrue(preferenceHelper.getPreferenceString("3:string") == "")

        assertEquals(0, PreferenceHelper.countPreferences(preferenceFilename, applicationContext, 3))
    }
}
