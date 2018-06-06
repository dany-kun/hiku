package cmd

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen
import platform.posix.system


actual fun runLocalCommand(
        commands: List<String>,
        useOutput: Boolean): String? {
    val cmd = commands.joinToString(" ")
    return if (useOutput) {
        val pipe = popen(cmd, "r")
        try {
            memScoped {
                val bufferLength = 64 * 1024
                val buffer = allocArray<ByteVar>(bufferLength)
                val string = StringBuilder()
                while (fgets(buffer, bufferLength, pipe) != null) {
                    string.append(buffer.toKString())
                }
                string.toString().trim()
            }
        } finally {
            pclose(pipe)
        }
    } else {
        system(cmd)
        null
    }

}
