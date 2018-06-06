package hiku

sealed class HttpResponse<out T> {
    data class Success<out T>(val data: T) : HttpResponse<T>()
    sealed class Error<out T>(open val exception: Throwable) : HttpResponse<T>() {
        class Auth<out T>(override val exception: Throwable) : Error<T>(exception)
        data class Unknown<out T>(override val exception: Throwable) : Error<T>(exception)
    }
}

expect suspend inline fun <reified B, reified R : Any> makePostRequest(url: String,
                                                                       headers: Map<String, String>,
                                                                       body: B): HttpResponse<R>