package hiku.git.lib

interface GitService {

    suspend fun pushToRemote(srcGitBranch: GitBranch.Local, remoteBranch: GitBranch.Remote)

    suspend fun getCurrentBranchName(): String

    suspend fun getRemoteInfo(remoteBranch: GitBranch.Remote): RemoteInfo

}