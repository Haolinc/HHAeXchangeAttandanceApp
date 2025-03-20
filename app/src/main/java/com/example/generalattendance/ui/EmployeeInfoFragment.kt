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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.generalattendance.R


private val personalCareNumList = listOf("101", "102", "106", "107", "108", "110", "111", "112", "113", "117")
private val nutrientNumList = listOf("202", "203", "204", "205", "300", "301", "411", "500", "501", "502", "506", "508", "509", "511")
private lateinit var currentViewModel: EmployeeInfoViewModel

@Composable
fun EmployeeInfoFragment(viewModel: EmployeeInfoViewModel){
    currentViewModel = viewModel
    SelectionPage()
}

@Composable
fun SelectionPage(){
    val employeeNum by currentViewModel.getEmployeeNum().observeAsState(initial = "")
    val callNum by currentViewModel.getCallNum().observeAsState(initial = "")
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
        item {
            GeneralText(stringResource(R.string.fragment_employee_info_header_basic_info))
        }
        item{
            HorizontalDivider(color = Color.Black, thickness = 2.dp)
        }
        //Employee Number Composable
        item{
            var text by remember {mutableStateOf(employeeNum)}
            var isError by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = text,
                onValueChange = {
                    if (it.length <= 6)
                        text = it
                    isError = it.length < 6
                    currentViewModel.setEmployeeNum(text)

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

        //Call Number Composable
        item{
            var text by remember {mutableStateOf(callNum)}
            var isError by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = text,
                onValueChange = {
                    if (it.length <= 10)
                        text = it
                    isError = it.length < 10
                    currentViewModel.setCallNum(text)
                },
                label = { Text(stringResource(R.string.fragment_employee_info_input_call_num)) },
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.error_text_call_number),
                            color = Color.Red
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.padding(top = 10.dp, bottom = 50.dp)
            )
        }
        item {
            GeneralText(stringResource(R.string.fragment_employee_info_header_work_num))
        }
        item{
            HorizontalDivider(color = Color.Black, thickness = 2.dp)
        }
        // Header
        item {
            GeneralText("Personal Care")
        }
        item {
            ButtonGrid(personalCareNumList)
        }
        // Header
        item {
            GeneralText("Nutrient")
        }
        item {
            ButtonGrid(nutrientNumList)
        }
    }

}

@Composable
fun ButtonGrid(buttonTextList: List<String>){
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),
        columns = GridCells.Fixed(3),
    ) {
        items(buttonTextList) { item ->
            DefaultButton(text = item)
        }
    }
}

@Composable
fun DefaultButton(text: String){
    val workNumList by currentViewModel.getWorkNumList().observeAsState(initial = emptyList())
    Button(onClick = { selectWorkNum(text) },
        modifier = Modifier.padding(all = 5.dp),
        colors = if (workNumList.contains(text)) ButtonDefaults.buttonColors() else ButtonDefaults.buttonColors(Color.LightGray)
    ){
        Text(text = text)
    }
}


private fun selectWorkNum(currentText: String){
    val existInList = currentViewModel.getWorkNumList().value?.contains(currentText)
    if (existInList == true){
        currentViewModel.removeWorkNum(currentText)
        println(currentViewModel.getWorkNumList().value.toString())
    }
    else{
        currentViewModel.addWorkNum(currentText)
        println(currentViewModel.getWorkNumList().value.toString())
    }

}

@Composable
fun GeneralText(text: String){
    Text(text = text, Modifier.padding(10.dp), fontSize = 20.sp)
}
