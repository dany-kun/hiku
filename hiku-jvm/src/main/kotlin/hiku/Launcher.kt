package hiku

import kotlinx.coroutines.experimental.runBlocking

fun main(args: Array<String>) {
    val command = findCommand(args)
    runBlocking { execute(command) }.also(::println)
}
