package hiku

import hiku.git.lib.GitBranch

fun findCommand(args: Array<String>): Command {
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

private fun createGitPRCommand(arguments: List<String>): Command.PR {
    fun String.toRemoteBranch() = split("/").let { GitBranch.Remote(it[1], it[0]) }
    return Command.PR(arguments[0].toRemoteBranch(), arguments[1].toRemoteBranch())
}