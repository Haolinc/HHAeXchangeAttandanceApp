package com.example.generalattendance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.example.generalattendance.AppDataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UIViewModel @Inject constructor(private val appDataStorage: AppDataStorage) : ViewModel() {
    private val _mutableLanguage = MutableLiveData(appDataStorage.getLanguage)
    private val _mutableIsFirstTime = MutableLiveData(appDataStorage.getIsFirstTime)

    // Language Functions
    fun getLanguage (): LiveData<String>{
        return _mutableLanguage.distinctUntilChanged()
    }

    fun setLanguage (language: String) {
        _mutableLanguage.value = language
    }

    // Is First Time
    fun getIsFirstTime (): LiveData<Boolean>{
        return _mutableIsFirstTime
    }

    fun setIsFirstTime (isFirstTime: Boolean) {
        _mutableIsFirstTime.value = isFirstTime
        CoroutineScope(Dispatchers.IO).launch{
            appDataStorage.setIsFirstTime(isFirstTime)
        }
    }
}