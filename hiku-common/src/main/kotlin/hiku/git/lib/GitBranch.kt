package hiku.git.lib

inline class GitBranchName(val name: String)

sealed class GitBranch(open val branchName: GitBranchName) {
    data class Local(override val branchName: GitBranchName) : GitBranch(branchName)
    data class Remote(override val branchName: GitBranchName, val remoteName: String) : GitBranch(branchName) {

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