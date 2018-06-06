package hiku

import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.startCoroutine


fun main(args: Array<String>) {  
    val command = findCommand(args)
    runCoroutine({ execute(command) }) {
        println(it)
    }
}

// Waiting for higher level coroutines lib to be available on Kotlin/Nqtive
internal fun <R> runCoroutine(block: suspend () -> R, callback: (R) -> Unit) {
    block.startCoroutine(object : Continuation<R> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resume(value: R) {
            callback(value)
        }

        override fun resumeWithException(exception: Throwable) {
            throw exception
        }
    })
}
