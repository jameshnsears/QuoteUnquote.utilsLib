package com.github.jameshnsears.quoteunquote.utils.logging

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class MethodLineLoggingTreeTest {
    @Test
    fun createStackElementTag() {
        val tree = MethodLineLoggingTree()

        val element1 = StackTraceElement("DeclaringClass", "MethodName", "FileName", 123)
        assertThat(invokeCreateStackElementTag(tree, element1), equalTo("MethodName, 123"))

        val element2 = StackTraceElement("OtherClass", "otherMethod", "OtherFile", 456)
        assertThat(invokeCreateStackElementTag(tree, element2), equalTo("otherMethod, 456"))
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
