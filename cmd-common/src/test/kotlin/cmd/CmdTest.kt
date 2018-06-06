package cmd

import kotlin.test.Test
import kotlin.test.assertEquals

class CmdTest {

    @Test
    fun testExecuteCommandAndReturnValue() {
        val hello = runLocalCommand(listOf("echo", "hello"), useOutput = true)
        assertEquals(hello, "hello")
    }

    @Test
    fun testExecuteCommandAndReturn() {
        val hello = runLocalCommand(listOf("echo", "hello"), useOutput = false)
        assertEquals(hello, null)
    }

}