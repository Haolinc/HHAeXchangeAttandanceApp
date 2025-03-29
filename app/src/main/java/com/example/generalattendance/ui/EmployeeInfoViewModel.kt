package com.example.generalattendance.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import com.example.generalattendance.AppDataStorage

class EmployeeInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val appDataStorage = AppDataStorage(getApplication())
    private val _mutableWorkNumList = MutableLiveData(appDataStorage.getWorkNumList)
    private val _mutableEmployeeNum = MutableLiveData(appDataStorage.getEmployeeNum)
    private val _mutableDialNum = MutableLiveData(appDataStorage.getDialNum)

    // Work Number List Functions
    fun getWorkNumList(): LiveData<List<String>> {
        return _mutableWorkNumList.distinctUntilChanged()
    }

    fun setWorkNumList(workNumList: List<String>) {
        _mutableWorkNumList.value = workNumList
    }

    //Employee Number Functions
    fun getEmployeeNum (): LiveData<String> {
        return _mutableEmployeeNum.distinctUntilChanged()
    }

    fun setEmployeeNum (employeeNum: String) {
        _mutableEmployeeNum.value = employeeNum
    }

    //Employee Number Functions
    fun getDialNum (): LiveData<String>{
        return _mutableDialNum.distinctUntilChanged()
    }

    fun setDialNum (dialNum: String) {
        _mutableDialNum.value = dialNum
    }

}