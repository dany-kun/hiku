package hiku.git.lib

sealed class GitBranch(open val branchName: String) {
    data class Local(override val branchName: String) : GitBranch(branchName)
    data class Remote(override val branchName: String, val remoteName: String) : GitBranch(branchName) {

        private var remoteInfo: RemoteInfo? = null

        suspend fun remoteInfo(gitService: GitService): RemoteInfo {
            return remoteInfo ?: gitService.getRemoteInfo(this)
                    .also { this.remoteInfo = it }
        }


        override fun toString(): String {
            return "$remoteName/$branchName"
        }
    }
}