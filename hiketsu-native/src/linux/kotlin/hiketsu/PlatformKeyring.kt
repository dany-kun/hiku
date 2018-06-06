package hiketsu

import hiketsu.Keyring
import libsecret.*
import kotlinx.cinterop.*

actual class PlatformKeyring actual constructor(private val keyringName: String) : Keyring {

    companion object {
        private const val SERVICE_ATTR = "service"
        private const val SERVICE_ATTR_VALUE = "hiku"
        private const val PROPERTY_ATTR = "property"
    }

    private fun schema(scope: AutofreeScope): SecretSchema {
        return scope.alloc<SecretSchema>().apply {
            name = "com.hiku.$keyringName".cstr.getPointer(scope)
            flags = SECRET_SCHEMA_NONE
            attributes[0].apply {
                name = SERVICE_ATTR.cstr.getPointer(scope)
                type = SECRET_SCHEMA_ATTRIBUTE_STRING
            }
            attributes[1].apply {
                name = PROPERTY_ATTR.cstr.getPointer(scope)
                type = SECRET_SCHEMA_ATTRIBUTE_STRING
            }
        }
    }

    private fun attributes(propertyAttributeValue: String): Array<String?> {
        return arrayOf(
                SERVICE_ATTR, SERVICE_ATTR_VALUE,
                PROPERTY_ATTR, propertyAttributeValue,
                null)
    }

    override fun storeEntry(keyName: String, keyValue: String) {
        memScoped {
            secret_password_store_sync(
                    schema(this).ptr, // schema: CValuesRef<SecretSchema>?
                    SECRET_COLLECTION_DEFAULT, // collection: String?
                    "$keyName for $keyringName", // label: String?
                    keyValue, // password: String?
                    null, //cancellable: CValuesRef<GCancellable>?
                    null, //error.ptr, //error: CValuesRef<CPointerVar<GError>>?,
                    *attributes(keyName)
            )
        }
    }

    override fun retrieveValue(keyName: String): String? {
        return memScoped {
            secret_password_lookup_sync(
                    schema(this).ptr,
                    null,
                    null, // error
                    *attributes(keyName)
            )?.toKString()
        }
    }

    override fun clear(keyName: String) {
        memScoped {
            secret_password_clear_sync(
                    schema(this).ptr,
                    null,
                    null,
                    *attributes(keyName)
            )
        }
    }
}