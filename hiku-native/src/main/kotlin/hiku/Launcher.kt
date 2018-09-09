package hiku

import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {  
    runBlocking {
        val command = findCommand(args)
        val result = execute(command)
        println(result)
    }
}
