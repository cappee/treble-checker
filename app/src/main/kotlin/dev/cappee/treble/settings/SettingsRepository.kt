package dev.cappee.treble.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore by preferencesDataStore("settings")

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    private val batteryFetchModeExperimental = booleanPreferencesKey("battery_fetch_mode_experimental")
    private val identifierOrder = intPreferencesKey("identifier_order")
    private val processorShownAs = stringPreferencesKey("processor_shown_as")

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

    fun getProcessorShownAs(): Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            return@map preferences[processorShownAs] ?: "Hardware"
        }

    suspend fun setProcessorShownAs(value: String) {
        dataStore.edit { preferences ->
            preferences[processorShownAs] = value
        }
    }

}