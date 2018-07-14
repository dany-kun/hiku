# HIKU

Experimental Kotlin multiplatform project to automatize some development tasks (currently only Github PR creation)

Platforms: JVM, Linux, Mac os

## Modules

### Hiku

Main client module to help/automatize/speed up Github PR creation (need a [Github token](https://github.com/settings/tokens))

### Cmd

Helper module to be able to execute some command line commands

### Hiketsu

Module to access the platform keystore (useful to store tokens)
TODO: refacto Jvm module to really use the keystore (currently simple key/value store)