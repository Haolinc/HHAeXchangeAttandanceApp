package com.example.generalattendance

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data")

class EmployeeInfoStorage(private val context: Context) {
    val EMPLOYEE_NUM = stringPreferencesKey("employee_num")
    val CALL_NUM = stringPreferencesKey("call_num")
    val WORK_NUM = stringPreferencesKey("work_num")
    val LANGUAGE = stringPreferencesKey("language")

    suspend fun setEmployeeNum(employeeNum: String){
        context.dataStore.edit { preferences ->
            preferences[EMPLOYEE_NUM] = employeeNum
        }
    }

    val getEmployeeNum: String = runBlocking {
        context.dataStore.data.map{
            preferences -> preferences[EMPLOYEE_NUM] ?: ""
        }.first()
    }

    suspend fun setCallNum(callNum: String){
        context.dataStore.edit { preferences ->
            preferences[CALL_NUM] = callNum
        }
    }

    val getCallNum: String = runBlocking {
        context.dataStore.data.map{
                preferences -> preferences[CALL_NUM] ?: ""
        }.first()
    }

    suspend fun setWorkNumList(workNumList: List<String>){
        context.dataStore.edit { preferences ->
            preferences[WORK_NUM] = workNumList.joinToString(separator = ",")
        }
    }

    val getWorkNumList: List<String> = runBlocking {
        val extractString = context.dataStore.data.map{
                preferences -> preferences[WORK_NUM] ?: ""
        }.first()
        if (extractString == "") {
            emptyList()
        }
        else{
            extractString.split(",")
        }
    }

    suspend fun setLanguage(language: String){
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }
    }

    val getLanguage: String = runBlocking {
        context.dataStore.data.map{
                preferences -> preferences[LANGUAGE] ?: context.resources.configuration.locales.get(0).language
        }.first()
    }
}