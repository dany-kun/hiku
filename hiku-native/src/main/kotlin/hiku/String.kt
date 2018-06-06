package hiku

import cmd.runLocalCommand

actual fun String.toBase64() : String {
    val command = "echo -n '$this' | base64"
    return runLocalCommand(listOf(command), useOutput = true)!!
}