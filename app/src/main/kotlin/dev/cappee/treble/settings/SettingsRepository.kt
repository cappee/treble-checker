package dev.cappee.treble.settings

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class SettingsRepository(context: Context) {

    private val dataStore = context.createDataStore("settings")

    private val batteryFetchModeExperimental = booleanPreferencesKey("battery_fetch_mode_experimental")
    private val identifierOrder = intPreferencesKey("identifier_order")
    private val processorShownAs = intPreferencesKey("chipset_shown_as")

    fun getBatteryFetchModeExperimental(): Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            return@map preferences[batteryFetchModeExperimental] ?: true
        }

    suspend fun setBatteryFetchModeExperimental(value: Int) {
        dataStore.edit { preferences ->
            preferences[batteryFetchModeExperimental] = when (value) {
                0 -> false
                1 -> true
                else -> true
            }
        }
    }

    fun getIdentifierOrder(): Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            return@map preferences[identifierOrder] ?: 0
        }

    suspend fun setIdentifierOrder(value: Int) {
        dataStore.edit { preferences ->
            preferences[identifierOrder] = value
        }
    }

    fun getProcessorShownAs(): Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            return@map preferences[processorShownAs] ?: 0
        }

    suspend fun setProcessorShownAs(value: Int) {
        dataStore.edit { preferences ->
            preferences[processorShownAs] = value
        }
    }

}