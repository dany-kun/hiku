package hiku.git.lib

import cmd.runLocalCommand

object CommandLineGitService : GitService {

    override suspend fun pushToRemote(srcGitBranch: GitBranch.Local, remoteBranch: GitBranch.Remote) {
        runLocalCommand(listOf(
                "git", "push",
                remoteBranch.remoteName,
                "${srcGitBranch.branchName}:${remoteBranch.branchName}"), useOutput = false)
    }

    override suspend fun getCurrentBranchName(): String {
        return runLocalCommand(listOf("git", "rev-parse", "--abbrev-ref", "HEAD"), useOutput = true)
                ?: throw RuntimeException("Could not find current branch name")
    }

    override suspend fun getRemoteInfo(remoteBranch: GitBranch.Remote): RemoteInfo {
        val remoteAddress = runLocalCommand(
                listOf("git", "config", "--get", "remote.${remoteBranch.remoteName}.url"),
                useOutput = true)
                ?: throw RuntimeException("Could not find remote info for $remoteBranch")
        val parts = remoteAddress.split("/")
        val remoteName = parts.last().replace(".git", "")
        val remoteOwner = parts[parts.size - 2]
                //Handle ssh
                .split(":")
                .last()
        return RemoteInfo(remoteOwner, remoteName)
    }
}