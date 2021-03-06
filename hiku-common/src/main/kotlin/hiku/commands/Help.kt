package hiku.commands

internal fun help(): String {
    return """
        | Commands:
        |         - pr remote1:branch1 remote2:branch2 : Push the current branch to remote1:branch1 and
        |         create a Github PR from remote1:branch1 to remote2:branch2
        |         - pr remote2:branch2 : Push the current branch to origin:current_branch_name and
        |         create a Github PR from origin:current_branch_name to remote2:branch2
        |         - current-branch : Show the current git branch
        |         - help: Help
        """.trimMargin()
}