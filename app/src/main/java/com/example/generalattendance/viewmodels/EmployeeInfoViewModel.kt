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
class EmployeeInfoViewModel @Inject constructor(private val appDataStorage: AppDataStorage) : ViewModel() {
    private val _mutableWorkNumList = MutableLiveData(appDataStorage.getWorkNumList)
    private val _mutableEmployeeNum = MutableLiveData(appDataStorage.getEmployeeNum)
    private val _mutableDialNum = MutableLiveData(appDataStorage.getDialNum)

    // Work Number List Functions
    fun getWorkNumList(): LiveData<List<String>> {
        return _mutableWorkNumList.distinctUntilChanged()
    }

    fun setWorkNumList(workNumList: List<String>) {
        _mutableWorkNumList.value = workNumList
        CoroutineScope(Dispatchers.IO).launch {
            appDataStorage.setWorkNumList(workNumList)
        }
    }

    //Employee Number Functions
    fun getEmployeeNum (): LiveData<String> {
        return _mutableEmployeeNum.distinctUntilChanged()
    }

    fun setEmployeeNum (employeeNum: String) {
        if (employeeNum != _mutableEmployeeNum.value) {
            _mutableEmployeeNum.value = employeeNum
            if (employeeNum.length == 6)
                CoroutineScope(Dispatchers.IO).launch {
                    appDataStorage.setEmployeeNum(employeeNum)
                }
        }
    }

    //Employee Number Functions
    fun getDialNum (): LiveData<String>{
        return _mutableDialNum.distinctUntilChanged()
    }

    fun setDialNum (dialNum: String) {
        if (dialNum != _mutableDialNum.value) {
            _mutableDialNum.value = dialNum
            if (dialNum.length == 10)
                CoroutineScope(Dispatchers.IO).launch {
                    appDataStorage.setDialNum(dialNum)
                }
        }
    }

}