package com.example.generalattendance.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.generalattendance.EmployeeInfoStorage

class EmployeeInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val employeeInfoStorage = EmployeeInfoStorage(getApplication())
    private val _mutableWorkNumList = MutableLiveData(employeeInfoStorage.getWorkNumList)
    private val _mutableEmployeeNum = MutableLiveData(employeeInfoStorage.getEmployeeNum)
    private val _mutableCallNum = MutableLiveData(employeeInfoStorage.getCallNum)
    private val _mutableLanguage = MutableLiveData(employeeInfoStorage.getLanguage)

    // Work Number List Functions
    fun getWorkNumList(): LiveData<List<String>>{
        return _mutableWorkNumList
    }

    fun addWorkNum (workNum: String) {
        val currentList = _mutableWorkNumList.value.orEmpty().toMutableList()
        currentList.add(workNum)
        _mutableWorkNumList.value = currentList
    }

    fun removeWorkNum (workNum: String) {
        val currentList = _mutableWorkNumList.value.orEmpty().toMutableList()
        currentList.remove(workNum)
        _mutableWorkNumList.value = currentList
    }

    //Employee Number Functions
    fun getEmployeeNum (): LiveData<String> {
        return _mutableEmployeeNum
    }

    fun setEmployeeNum (employeeNum: String) {
        _mutableEmployeeNum.value = employeeNum
    }

    //Employee Number Functions
    fun getCallNum (): LiveData<String>{
        return _mutableCallNum
    }

    fun setCallNum (callNum: String) {
        _mutableCallNum.value = callNum
    }

    //Employee Number Functions
    fun getLanguage (): LiveData<String>{
        return _mutableLanguage
    }

    fun setLanguage (language: String) {
        _mutableLanguage.value = language
    }
}