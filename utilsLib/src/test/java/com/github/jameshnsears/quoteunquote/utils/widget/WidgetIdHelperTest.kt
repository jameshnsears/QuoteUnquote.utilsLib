package com.github.jameshnsears.quoteunquote.utils.widget

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.jameshnsears.quoteunquote.utils.logging.ShadowLoggingHelper
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.S])
class WidgetIdHelperTest : ShadowLoggingHelper() {
    @Test
    fun `also tests ShadowLoggingHelper`() {
        assertEquals(-1, WidgetIdHelper.WIDGET_ID_01)
        assertEquals(-2, WidgetIdHelper.WIDGET_ID_02)
    }
}
