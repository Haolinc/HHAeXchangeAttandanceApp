package com.example.generalattendance.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import com.example.generalattendance.AppDataStorage

class UIViewModel(application: Application) : AndroidViewModel(application) {
    private val appDataStorage = AppDataStorage(getApplication())
    private val _mutableLanguage = MutableLiveData(appDataStorage.getLanguage)

    // Language Functions
    fun getLanguage (): LiveData<String>{
        return _mutableLanguage.distinctUntilChanged()
    }

    fun setLanguage (language: String) {
        _mutableLanguage.value = language
    }
}