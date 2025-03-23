package com.example.generalattendance.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.generalattendance.AppDataStorage
import com.example.generalattendance.Clocking
import com.example.generalattendance.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun ClockingFragment(viewModel: EmployeeInfoViewModel, isCallPermissionGranted: Boolean){
    val appDataStorage = AppDataStorage(LocalContext.current)
    LaunchedEffect(true) {
        CoroutineScope(Dispatchers.IO).launch{
            if (appDataStorage.getIsFirstTime){
                appDataStorage.setIsFirstTime(false)
            }
        }
    }
    val workNumList by viewModel.getWorkNumList().observeAsState(emptyList())
    val callNumber by viewModel.getCallNum().observeAsState("")
    val employeeNumber by viewModel.getEmployeeNum().observeAsState("")
    val buttonState by remember (workNumList, employeeNumber, callNumber, isCallPermissionGranted) {
        derivedStateOf {
            workNumList.isNotEmpty() && employeeNumber.length == 6 && callNumber.length == 10 && isCallPermissionGranted
        }
    }
    val context = LocalContext.current
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "attendance:wakelock")

    Column(
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1.25f).fillMaxWidth().padding(10.dp)
        ){
            val emptyDefaultText = stringResource(R.string.fragment_clocking_content_text_default)
            val employeeNumText = if (employeeNumber == "") emptyDefaultText else employeeNumber
            EmployeeInfoSection(title = stringResource(R.string.fragment_clocking_header_employee_number), content = employeeNumText)
            val callNumText = if (callNumber == "") emptyDefaultText else callNumber
            EmployeeInfoSection(title = stringResource(R.string.fragment_clocking_header_call_number), content = callNumText)
            val workNumText = if (workNumList.isEmpty()) emptyDefaultText else workNumList.sorted().toString()
            EmployeeInfoSection(title = stringResource(R.string.fragment_clocking_header_work_number), content = workNumText)
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ){
            if (workNumList.isEmpty())
                ErrorText(text = stringResource(R.string.fragment_clocking_error_text_work_number))
            if (employeeNumber.length != 6)
                ErrorText(text = stringResource(R.string.error_text_employee_number))
            if (callNumber.length != 10)
                ErrorText(text = stringResource(R.string.error_text_call_number))
            if (!isCallPermissionGranted)
                ErrorText(text = stringResource(R.string.error_text_call_permission))

            ClockingButton(
                {
                    val clocking = Clocking(employeeNumber, callNumber, workNumList.sorted())
                    initiateCall(clocking.getFullOnClockUriCode(), true, context, wakeLock, workNumList.size)
                },
                stringResource(R.string.fragment_clocking_check_in_button_text),
                buttonState
            )
            ClockingButton(
                {
                    val clocking = Clocking(employeeNumber, callNumber, workNumList.sorted())
                    initiateCall(clocking.getFullOffClockUriCode(), false, context, wakeLock, workNumList.size)
                },
                stringResource(R.string.fragment_clocking_check_out_button_text),
                buttonState
            )
        }

    }
}

private fun initiateCall(uri: Uri, isOnClock: Boolean, context: Context, wakeLock: WakeLock, workNumListSize: Int){
    val dialIntent = Intent(Intent.ACTION_CALL)
    dialIntent.setData(uri)
    // Release lock
    if (wakeLock.isHeld) {
        wakeLock.release()
    }
    // Start locking
    val lockTime = calculateTotalWaitTime(isOnClock, workNumListSize)
    Log.i("MainActivity","locking for $lockTime seconds")
    wakeLock.acquire(lockTime*1000L)

    context.startActivity(dialIntent)
    println(uri)
}

private fun calculateTotalWaitTime(onClock: Boolean = true, workNumListSize: Int): Int {
    val initialWaitTime = 38
    if (onClock)
        return initialWaitTime
    return initialWaitTime + (workNumListSize + 1) * 7   // add 1 for 000 number set
}

@Composable
fun ClockingButton(function:() -> Unit, buttonText: String, buttonState: Boolean){
    Button(
        onClick = function,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        enabled = buttonState
    ){
        Text(text = buttonText, fontSize = 20.sp)
    }
}

@Composable
fun EmployeeInfoSection(title: String, content: String){
    Text(text = title, fontSize = 20.sp, modifier = Modifier.padding(10.dp))
    Box(
        modifier = Modifier.border(2.dp, Color.Blue),
        contentAlignment = Alignment.Center
    ){
        Text(text = content, fontSize = 15.sp, modifier = Modifier.padding(10.dp))
    }
}

@Composable
fun ErrorText(text: String){
    Text(text = text, fontSize = 15.sp, color = Color.Red, modifier = Modifier.padding(5.dp))
}