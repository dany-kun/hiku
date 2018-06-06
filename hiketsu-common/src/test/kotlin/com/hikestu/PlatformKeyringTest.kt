package com.hikestu

import hiketsu.PlatformKeyring
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlatformKeyringTest {

    private val keyring = PlatformKeyring("test")
    private val keyName = "testKey"
    private val keyValue = "keyValue"

    @BeforeTest
    fun setup() {
        keyring.clear(keyName)
    }

    @Test
    fun addKeyIntoKeyring() {
        val value = keyring.retrieveValue(keyName)
        assertTrue(value == null)
        keyring.storeEntry(keyName, keyValue)
        val value2 = keyring.retrieveValue(keyName)
        assertTrue(value2 == keyValue)
    }

    @Test
    fun clearKeyring() {
        keyring.storeEntry(keyName, keyValue)
        keyring.clear(keyName)
        val value = keyring.retrieveValue(keyName)
        assertTrue(value == null)
    }
}