package hiketsu

interface Keyring {

    fun storeEntry(keyName: String, keyValue: String)

    fun retrieveValue(keyName: String): String?

    fun clear(keyName: String)
}