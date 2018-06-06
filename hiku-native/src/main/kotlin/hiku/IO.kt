package hiku

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import kotlinx.cinterop.signExtend
import platform.posix.*

actual suspend fun requestInput(message: String, hideInput: Boolean) : String {
    return memScoped {
        val bufferLength = 64 * 1024
        val buffer = allocArray<ByteVar>(bufferLength)
        if (hideInput) {
            strncpy(buffer, getpass(message)?.toKString(), bufferLength.signExtend())
        } else {
            print(message)
            scanf("%s", buffer)
        }
        buffer.toKString()
    }
}