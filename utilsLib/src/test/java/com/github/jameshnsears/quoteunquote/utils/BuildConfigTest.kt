package com.github.jameshnsears.quoteunquote.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class BuildConfigTest {
    @Test
    fun `that BuildConfig populated from gradle`() {
        assertEquals("", 36, BuildConfig.APPCENTER_KEY.length)
    }
}