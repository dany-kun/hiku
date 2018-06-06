package hiku.git.github

import hiketsu.Keyring
import hiku.HttpResponse
import hiku.git.RemotePullRequestService
import hiku.git.lib.CommandLineGitService
import hiku.git.lib.GitBranch
import hiku.git.lib.RemoteInfo
import hiku.makePostRequest
import hiku.requestInput
import hiku.toBase64

class GithubService(owner: String,
                    repo: String,
                    private val keyring: Keyring) : RemotePullRequestService {

    companion object {
        const val KEYRING_NAME = "HIKU_GITHUB"
        private const val KEYRING_USERNAME_kEY = "HIKU_GITHUB_USERNAME"
        private const val KEYRING_USERTOKEN_KEY = "HIKU_GITHUB_TOKEN"
    }

    constructor(remoteInfo: RemoteInfo, keyring: Keyring)
            : this(remoteInfo.owner, remoteInfo.repoName, keyring)

    private val baseUrl = "https://api.github.com/repos/$owner/$repo"

    data class PullRequestBody(val title: String, val head: String, val base: String)
    data class CreatedPullRequest(val html_url: String)

    override suspend fun createRemotePr(srcGitBranch: GitBranch.Remote, destGitBranch: GitBranch.Remote): String {
        val response = sendPRRequest(srcGitBranch, destGitBranch)
        return when (response) {
            is HttpResponse.Success<CreatedPullRequest> -> response.data.html_url
            is HttpResponse.Error -> {
                println(response.exception)
                when (response) {
                    is HttpResponse.Error.Auth -> {
                        keyring.clear(KEYRING_USERNAME_kEY)
                        keyring.clear(KEYRING_USERTOKEN_KEY)
                        "Invalid Credits"
                    }
                    is HttpResponse.Error.Unknown -> throw response.exception
                }
            }
        }
    }

    private suspend fun sendPRRequest(srcGitBranch: GitBranch.Remote,
                                      destGitBranch: GitBranch.Remote): HttpResponse<CreatedPullRequest> {
        val url = "$baseUrl/pulls"
        val srcFullName = formatSrcRemoteBranch(srcGitBranch)
        val body = PullRequestBody(srcGitBranch.branchName, srcFullName, destGitBranch.branchName)
        return makePostRequest(url, mapOf(githubOAuthHeader(), "Content-Type" to "application/json"), body)
    }

    private suspend fun formatSrcRemoteBranch(branch: GitBranch.Remote): String {
        return "${branch.remoteInfo(CommandLineGitService).owner}:${branch.branchName}"
    }

    private suspend fun githubOAuthHeader(): Pair<String, String> {
        val (username, password) = getUserCredits()
        val auth = "$username:$password".toBase64()
        return "Authorization" to "Basic $auth"
    }

    private suspend fun getUserCredits(): Pair<String, String> {
        val username = findUserInfo(KEYRING_USERNAME_kEY, "Github username: ", false)
        val password = findUserInfo(KEYRING_USERTOKEN_KEY, "Github token: ", true)
        return Pair(username, password)
    }

    private suspend fun findUserInfo(infoName: String, infoMessage: String, hideInput: Boolean): String {
        return keyring.retrieveValue(infoName)
                ?: requestInput(infoMessage, hideInput).also {
                    keyring.storeEntry(infoName, it)
                }
    }

}