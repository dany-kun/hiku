package hiku

import cmd.runLocalCommand
import hiku.HttpResponse
import hiku.git.github.GithubService
import kotlin.text.*

actual suspend inline fun <reified B, reified R : Any> makePostRequest(url: String,
                                                                       headers: Map<String, String>,
                                                                       body: B): HttpResponse<R> {
    if (body !is GithubService.PullRequestBody) {
        throw RuntimeException("Unknown request body: $body; can currently handle only github PR post request")
    }
    val prBody = body as GithubService.PullRequestBody
    val command = createCurlCommand(prBody, headers, url)
    val result = runLocalCommand(listOf(command), useOutput = true)
            ?.dropWhile { it == '0' } //TODO understand where those 0 are coming from
            ?: throw IllegalStateException("Got null response from http request")
    val trimmedResult = result.dropLastWhile { !it.isDigit() }.trim()
    val responseCode = trimmedResult.takeLast(3).toInt()
    val responseBody = trimmedResult.dropLast(3)
    return if (responseCode == 404) {
        HttpResponse.Error.Auth(IllegalStateException(responseBody))
    } else if (responseCode >= 200 && responseCode < 400) {
        // Dirty
        val htmlUrl = extractHtmlUrl(responseBody)
        HttpResponse.Success(GithubService.CreatedPullRequest(htmlUrl)) as HttpResponse<R>
    } else {
        println(responseCode)
        println(responseBody)
        kotlin.system.exitProcess(0)
        // HttpResponse.Error.Unknown<R>(IllegalStateException(responseBody))
    }
}

fun extractHtmlUrl(responseBody: String): String {
    val key = "html_url"
    val beginning = responseBody.indexOf(key) + key.length
    return responseBody.drop(beginning).dropWhile { !it.isLetter() }.takeWhile { it != '"' }
}


fun createCurlCommand(body: GithubService.PullRequestBody, headers: Map<String, String>, url: String): String {
    val serializedBody = serializeBody(body)
    val serializedHeaders = headers.entries.map { " -H \"${it.key}:${it.value}\"" }.joinToString("")
    val command = """
        |curl --silent -w %{http_code} -X POST
        |-d "$serializedBody"
        |-H "Accept-Encoding:compress;q=0.5, gzip;q=1.0"$serializedHeaders
        |$url
        """.trimMargin().replace("\n", " ")
    return command
}

fun serializeBody(body: GithubService.PullRequestBody): String {
    if (body is GithubService.PullRequestBody) {
        return """
            |{
            |\"title\":\"${body.title}\",
            |\"head\":\"${body.head}\",
            |\"base\":\"${body.base}\"
            |}
            """.trimMargin().replace("\n", "")
    } else {
        throw RuntimeException("Unknown request body: $body")
    }
}