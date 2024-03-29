package com.github.jameshnsears.quoteunquote.utils.audit

import android.os.Build
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.concurrent.ConcurrentHashMap

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class AuditEventHelperTest {
    @Test
    fun `demonstrate invocation`() {
        AuditEventHelper.createInstance(getApplicationContext())
        val properties: ConcurrentHashMap<String, String> = ConcurrentHashMap()
        properties["a"] = "b"
        properties["0123456789001234567890012345678900123456789001234567890"] =
            "012345678900123456789001234567890012345678900123456789001234567890012345678900123456789001234567890012345678900123456789001234567890"
        AuditEventHelper.auditEvent("", properties)
    }
}
