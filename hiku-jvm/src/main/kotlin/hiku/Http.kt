package hiku

import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.moshi.moshiDeserializerOf
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

actual suspend inline fun <reified B, reified R : Any> makePostRequest(url: String,
                                                                       headers: Map<String, String>,
                                                                       body: B): HttpResponse<R> {
    val serializedBody = moshi.adapter<B>(B::class.java).toJson(body)
    return suspendCancellableCoroutine { continuation ->
        val request = url.httpPost()
                .header(headers)
                .body(serializedBody)
        println(request.cUrlString())
        request.responseObject(moshiDeserializerOf<R>(), { _, response, result ->
            if (response.statusCode == 404) {
                continuation.resume(HttpResponse.Error.Auth(result.component2()?.exception
                        ?: RuntimeException("No error associated with a 404")))
            }
            result.fold({ continuation.resume(HttpResponse.Success(it)) }, {
                continuation.resume(HttpResponse.Error.Unknown(it))
            })
        })
    }
}