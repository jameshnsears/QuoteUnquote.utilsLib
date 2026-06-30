package com.github.jameshnsears.quoteunquote.utils.logging

import org.junit.Assert.assertEquals
import org.junit.Test

class MethodLineLoggingTreeTest {
    @Test
    fun createStackElementTag() {
        val tree = MethodLineLoggingTree()

        val element1 = StackTraceElement("DeclaringClass", "MethodName", "FileName", 123)
        assertEquals("MethodName, 123", invokeCreateStackElementTag(tree, element1))

        val element2 = StackTraceElement("OtherClass", "otherMethod", "OtherFile", 456)
        assertEquals("otherMethod, 456", invokeCreateStackElementTag(tree, element2))
    }

    private fun invokeCreateStackElementTag(
        tree: MethodLineLoggingTree,
        element: StackTraceElement,
    ): String? {
        val method = tree.javaClass.getDeclaredMethod("createStackElementTag", StackTraceElement::class.java)
        method.isAccessible = true
        return method.invoke(tree, element) as String?
    }
}
