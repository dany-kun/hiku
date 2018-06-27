package hiketsu

import kotlinx.cinterop.*
import platform.Security.*
import platform.darwin.ByteVar
import platform.darwin.OSStatus
import platform.darwin.UInt32Var

actual class PlatformKeyring actual constructor(private val keyringName: String) : Keyring {

    companion object {
        private const val SERVICE_NAME = "hiku"
    }

    override fun storeEntry(keyName: String, keyValue: String) {
        memScoped {
            SecKeychainAddGenericPassword(null,
                SERVICE_NAME.length,
                SERVICE_NAME,
                keyName.length,
                keyName,
                keyValue.length,
                keyValue.cstr,
                null)
        }
    }

    override fun retrieveValue(keyName: String): String? {
        return memScoped { findItem(this, keyName, null) }
    }

    override fun clear(keyName: String) {
        memScoped {
            val itemRef = alloc<SecKeychainItemRefVar>()
            val value = findItem(this, keyName, itemRef)
            if (value != null) {
                SecKeychainItemDelete(itemRef.value)
            }
        }
    }

    private fun findItem(scope: MemScope,
                         keyName: String,
                         pointerToItem: SecKeychainItemRefVar?): String? {
        val pwdLength = scope.alloc<UInt32Var>()
        val pwd = scope.alloc<COpaquePointerVar>()
        SecKeychainFindGenericPassword(null,
            SERVICE_NAME.length,
            SERVICE_NAME,
            keyName.length,
            keyName,
            pwdLength.ptr,
            pwd.ptr,
            pointerToItem?.ptr
        )
        return pwd.value
            ?.reinterpret<ByteVar>()
            ?.toKString()
            ?.take(pwdLength.value)
    }
}