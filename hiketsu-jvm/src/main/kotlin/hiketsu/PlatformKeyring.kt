package hiketsu

import jetbrains.exodus.ArrayByteIterable
import jetbrains.exodus.bindings.StringBinding
import jetbrains.exodus.crypto.streamciphers.SALSA20_CIPHER_ID
import jetbrains.exodus.env.*

actual class PlatformKeyring actual constructor(keyringName: String) : Keyring {

    private val dbPath = "tmp/.hiketsu.$keyringName"

    private fun <T> useDB(block: Store.(Transaction) -> T): T {
        val db = Environments
                .newInstance(dbPath, EnvironmentConfig()
                        .setCipherId(SALSA20_CIPHER_ID)
                        .setCipherKey("123102030405060708090a0b0c0d0e0f000102030405060708090a0b0c0d0e0f")
                        .setCipherBasicIV(10)
                )
        return db.computeInTransaction {
            val store = db.openStore("hiku", StoreConfig.WITHOUT_DUPLICATES, it)
            store.block(it)
        }.also { db.close() }

    }

    private fun String.toStorable(): ArrayByteIterable {
        return StringBinding.stringToEntry(this)
    }


    override fun storeEntry(keyName: String, keyValue: String) {
        useDB {
            put(it, keyName.toStorable(), keyValue.toStorable())
        }
    }

    override fun retrieveValue(keyName: String): String? {
        return useDB { get(it, keyName.toStorable()) }?.let { StringBinding.entryToString(it) }
    }

    override fun clear(keyName: String) {
        useDB { delete(it, keyName.toStorable()) }
    }

}