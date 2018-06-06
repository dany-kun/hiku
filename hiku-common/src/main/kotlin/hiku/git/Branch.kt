package hiku.git

import hiku.git.lib.CommandLineGitService
import hiku.git.lib.GitBranch

private val forbiddenBranches = listOf("master")

interface RemotePullRequestService {
    suspend fun createRemotePr(srcGitBranch: GitBranch.Remote, destGitBranch: GitBranch.Remote): String
}

suspend fun createPR(srcGitBranch: GitBranch.Local,
                     srcRemoteBranch: GitBranch.Remote,
                     destGitBranch: GitBranch.Remote,
                     gitService: RemotePullRequestService): String {
    val remoteBranch = updateRemote(srcGitBranch, srcRemoteBranch)
    return gitService.createRemotePr(remoteBranch, destGitBranch)
}

private suspend fun updateRemote(srcGitBranch: GitBranch.Local, remoteBranch: GitBranch.Remote): GitBranch.Remote {
    val srcBranchName = srcGitBranch.branchName
    require(!forbiddenBranches.contains(srcBranchName)) {
        println("Cannot use this command on branch $srcBranchName")
    }
    println("Push $srcBranchName to $remoteBranch")
    CommandLineGitService.pushToRemote(srcGitBranch, remoteBranch)
    return remoteBranch

}
