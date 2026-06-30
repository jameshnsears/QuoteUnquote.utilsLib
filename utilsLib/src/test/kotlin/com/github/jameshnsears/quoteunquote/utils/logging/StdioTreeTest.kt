package com.github.jameshnsears.quoteunquote.utils.logging

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class StdioTreeTest {
    @Test
    fun log() {
        val tree = StdioTree()
        val outContent = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outContent))

        try {
            val method =
                tree.javaClass.getDeclaredMethod(
                    "log",
                    Int::class.javaPrimitiveType,
                    String::class.java,
                    String::class.java,
                    Throwable::class.java,
                )
            method.isAccessible = true
            method.invoke(tree, 3, "TAG", "Message", null)

            val output = outContent.toString()
            // Output format: "HH:mm:ss.SSS [TAG] Message"
            assertTrue(output.contains("[TAG] Message"))
        } finally {
            System.setOut(originalOut)
        }
    }
}
