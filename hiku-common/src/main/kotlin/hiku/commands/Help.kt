package hiku.commands

internal fun help(): String {
    return """
        | Commands:
        |         - pr remote1/branch1 remote2/branch2 : Push the current branch to remote1/branch1 and
        |         create a Github PR from remote1/branch1 to remote2/branch2
        |         - current-branch : Show the current git branch
        |         - help: Help
        """.trimMargin()
}