package hiku

import hiku.commands.help
import hiku.commands.pushAndCreatePr
import hiku.git.lib.CommandLineGitService
import hiku.git.lib.GitBranch

sealed class Command {
    object Help : Command()
    object CurrentBranch : Command()
    data class PR(val from: GitBranch.Remote,
                  val to: GitBranch.Remote) : Command()
}

suspend fun execute(command: Command): String {
    return when (command) {
        Command.Help -> help()
        is Command.PR -> pushAndCreatePr(command)
        Command.CurrentBranch -> CommandLineGitService.getCurrentBranchName()
    }
}