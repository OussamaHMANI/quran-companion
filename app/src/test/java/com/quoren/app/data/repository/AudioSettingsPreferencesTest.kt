package com.quoren.app.data.repository

import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AudioSettingsPreferencesTest {

    private val reciterKey = stringPreferencesKey("default_reciter")

    @Test
    fun clearingDefaultReciterRemovesPersistedPreference() {
        val preferences = mutablePreferencesOf(reciterKey to "reciter-a")

        preferences.setDefaultReciter(null)

        assertFalse(preferences.asMap().containsKey(reciterKey))
    }

    @Test
    fun settingDefaultReciterReplacesPersistedPreference() {
        val preferences = mutablePreferencesOf(reciterKey to "reciter-a")

        preferences.setDefaultReciter("reciter-b")

        assertEquals("reciter-b", preferences[reciterKey])
    }
}