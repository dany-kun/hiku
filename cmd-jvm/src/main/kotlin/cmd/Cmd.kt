package cmd

import java.util.concurrent.TimeUnit
import kotlin.coroutines.suspendCoroutine

actual fun runLocalCommand(
        commands: List<String>,
        useOutput: Boolean): String? {
    val process = ProcessBuilder(commands).apply {
        if (!useOutput) redirectOutput(ProcessBuilder.Redirect.INHERIT)
        redirectError(ProcessBuilder.Redirect.INHERIT)
    }

    val startedProcess = process.start()
    val result = if (useOutput) extract(startedProcess) else null

    startedProcess.waitFor(15, TimeUnit.SECONDS)

    val exitValue = startedProcess.exitValue()
    if (exitValue != 0) {
        throw  RuntimeException("Command ${process.command().joinToString(" ")} failed")
    } else {
        return result
    }
}

fun extract(startedProcess: Process): String {
    return startedProcess.inputStream.bufferedReader().readText().trim()
}


