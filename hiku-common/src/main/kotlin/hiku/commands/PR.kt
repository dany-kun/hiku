package hiku.commands

import hiketsu.PlatformKeyring
import hiku.Command
import hiku.git.createPR
import hiku.git.github.GithubService
import hiku.git.lib.CommandLineGitService
import hiku.git.lib.GitBranch
import hiku.git.lib.GitBranchName

suspend fun pushAndCreatePr(command: Command.PushPR): String {
    val currentBranchName = CommandLineGitService.getCurrentBranchName()
    val remoteRepoDestination = CommandLineGitService.getRemoteInfo(command.to)
    return createPR(
            GitBranch.Local(GitBranchName(currentBranchName)),
            command.from,
            command.to,
            GithubService(
                    remoteRepoDestination,
                    PlatformKeyring(GithubService.KEYRING_NAME)))
}