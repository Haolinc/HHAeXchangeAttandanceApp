package com.example.generalattendance.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.generalattendance.AppDataStorage
import com.example.generalattendance.R
import com.example.generalattendance.viewmodels.EmployeeInfoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private val personalCareNumList = listOf("100", "101", "102", "103", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117")
private val nutritionNumList = listOf("201", "202", "203", "204", "205", "206", "207", "208")
private val activityNumList = listOf("300", "301", "302", "305", "306", "311")
private val houseKeepingNumList = listOf("409", "410", "411", "413", "500", "501")
private val specialNeedsNumList = listOf("502", "506", "508", "509", "511", "514")
private val workNumMap = mapOf(
    "Personal Care Tasks" to personalCareNumList,
    "Nutrition" to nutritionNumList,
    "Activity" to activityNumList,
    "Housekeeping" to houseKeepingNumList,
    "SpecialNeeds" to specialNeedsNumList
)
private val LocalViewModel = compositionLocalOf<EmployeeInfoViewModel> {
    error("EmployeeInfoViewModel not provided")
}

@Composable
fun EmployeeInfoFragment(viewModel: EmployeeInfoViewModel){
    CompositionLocalProvider(LocalViewModel provides viewModel) {
        SelectionPage()
    }
}

@Composable
fun SelectionPage(){
    val appDataStorage = AppDataStorage(LocalContext.current)
    val currentViewModel = LocalViewModel.current
    val employeeNum by currentViewModel.getEmployeeNum().observeAsState("")
    val dialNum by currentViewModel.getDialNum().observeAsState("")
    val localFocusManager = LocalFocusManager.current
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item("Basic Information Header") {
            GeneralText(stringResource(R.string.fragment_employee_info_header_basic_info))
            HorizontalDivider(color = Color.Black, thickness = 2.dp)
        }
        //Employee Number Composable
        item("Employee Number") {
            var text by remember {mutableStateOf(employeeNum)}
            var isError by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = text,
                onValueChange = {
                    if (it.length <= 6) {
                        text = it
                        if (it.length == 6) {
                            CoroutineScope(Dispatchers.IO).launch {
                                appDataStorage.setEmployeeNum(it)
                            }
                        }
                        currentViewModel.setEmployeeNum(it)
                    }
                    isError = it.length < 6
                },
                label = { Text(stringResource(R.string.fragment_employee_info_input_employee_num)) },
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.error_text_employee_number),
                            color = Color.Red
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        //Dial Number Composable
        item("Dial Number") {
            var text by remember {mutableStateOf(dialNum)}
            var isError by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = text,
                onValueChange = {
                    if (it.length <= 10) {
                        text = it
                        if (it.length == 10) {
                            CoroutineScope(Dispatchers.IO).launch {
                                appDataStorage.setDialNum(it)
                            }
                        }
                        currentViewModel.setDialNum(it)
                    }
                    isError = it.length < 10
                },
                label = { Text(stringResource(R.string.fragment_employee_info_input_dial_num)) },
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.error_text_dial_number),
                            color = Color.Red
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.padding(top = 10.dp, bottom = 50.dp)
            )
        }
        item ("Work Number Header") {
            GeneralText(stringResource(R.string.fragment_employee_info_header_work_num))
            HorizontalDivider(color = Color.Black, thickness = 2.dp)
        }

        workNumMap.map{(headerText, currentWorkNumList) ->
            item (headerText){
                WorkNumSelection(headerText, currentWorkNumList, appDataStorage)
            }
        }
    }

}

@Composable
fun ButtonGrid(buttonTextList: List<String>, appDataStorage: AppDataStorage){
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),
        columns = GridCells.Fixed(3),
    ) {
        items(buttonTextList) { item ->
            DefaultButton(text = item, appDataStorage = appDataStorage)
        }
    }
}

@Composable
fun DefaultButton(text: String, appDataStorage: AppDataStorage){
    val currentViewModel = LocalViewModel.current
    val workNumList by currentViewModel.getWorkNumList().observeAsState(emptyList())
    Button(onClick = { selectWorkNum(text, currentViewModel, workNumList, appDataStorage) },
        modifier = Modifier.padding(all = 5.dp),
        colors = if (workNumList.contains(text)) ButtonDefaults.buttonColors() else ButtonDefaults.buttonColors(Color.LightGray)
    ){
        Text(text = text)
    }
}


private fun selectWorkNum(currentText: String, currentViewModel: EmployeeInfoViewModel, workList: List<String>, appDataStorage: AppDataStorage){
    val existInList = workList.contains(currentText)
    val newWorkList = if (existInList) workList - currentText else workList + currentText
    currentViewModel.setWorkNumList(newWorkList)
    CoroutineScope(Dispatchers.IO).launch {
        appDataStorage.setWorkNumList(newWorkList)
    }
}

@Composable
fun GeneralText(text: String){
    Text(text = text, Modifier.padding(10.dp), fontSize = 20.sp)
}

@Composable
fun WorkNumSelection(headerText: String, currentWorkNumList: List<String>, appDataStorage: AppDataStorage){
    GeneralText(headerText)
    ButtonGrid(currentWorkNumList, appDataStorage)
}
