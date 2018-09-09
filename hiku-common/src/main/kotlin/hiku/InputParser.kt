package hiku

import hiku.git.lib.CommandLineGitService
import hiku.git.lib.GitBranch
import hiku.git.lib.GitBranchName

suspend fun findCommand(args: Array<String>): Command {
    return try {
        val action = args[0].toLowerCase()
        when (action) {
            "pr" -> createGitPRCommand(args.drop(1))
            "current-branch" -> Command.CurrentBranch
            "help" -> Command.Help
            else -> {
                println("Unknown command: $action")
                Command.Help
            }
        }
    } catch (e: Exception) {
        println(e.message)
        Command.Help
    }
}

private suspend fun createGitPRCommand(arguments: List<String>): Command.PushPR {
    fun String.toRemoteBranch() = split(":").let { GitBranch.Remote(
            GitBranchName(it[1]),
            it[0]) }
    return when (arguments.size) {
        1 -> {
            val currentBranchName = CommandLineGitService.getCurrentBranchName()
            Command.PushPR(GitBranch.Remote(GitBranchName(currentBranchName), "origin"), arguments[0].toRemoteBranch())
        }
        2 -> Command.PushPR(arguments[0].toRemoteBranch(), arguments[1].toRemoteBranch())
        else -> {
            throw IllegalStateException("Cannot create a pr with arguments ${arguments.joinToString(", ")}")
        }
    }
}