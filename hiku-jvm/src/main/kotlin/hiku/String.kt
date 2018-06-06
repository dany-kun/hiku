package hiku

import com.github.kittinunf.fuel.util.Base64

actual fun String.toBase64() : String {
    val encodedAuth = Base64.encode(toByteArray(), Base64.NO_WRAP)
    return String(encodedAuth)
}