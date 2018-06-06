package hiku

import java.util.*

actual suspend fun requestInput(message: String, hideInput: Boolean): String {
    return if (hideInput) {
        System.console().run {
            readPassword(message).run { String(this) }
        }
    } else {
        val reader = Scanner(System.`in`)  // Reading from System.in
        println(message)
        val input = reader.nextLine() // Scans the next token of the input as an int.
        //once finished
        reader.close()
        input
    }
}