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
import com.example.generalattendance.Clocking
import com.example.generalattendance.R

private lateinit var currentViewModel: EmployeeInfoViewModel
private lateinit var wakeLock: WakeLock
private lateinit var clocking: Clocking

@Composable
fun ClockingFragment(viewModel: EmployeeInfoViewModel, isCallPermissionGranted: Boolean){
    currentViewModel = viewModel
    val workNumList by currentViewModel.getWorkNumList().observeAsState(initial = emptyList())
    val callNumber by currentViewModel.getCallNum().observeAsState(initial = "")
    val employeeNumber by currentViewModel.getEmployeeNum().observeAsState(initial = "")
    val buttonState by remember (workNumList, employeeNumber, callNumber, isCallPermissionGranted) {
        derivedStateOf {
            workNumList.isNotEmpty() && employeeNumber.length == 6 && callNumber.length == 10 && isCallPermissionGranted
        }
    }
    val sortedWorkNumList = workNumList.sorted()
    clocking = Clocking(employeeNumber, callNumber, sortedWorkNumList)
    val context = LocalContext.current

    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "attendance:wakelock")

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
            val workNumText = if (sortedWorkNumList.isEmpty()) emptyDefaultText else sortedWorkNumList.toString()
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
                    initiateCall(clocking.getFullOnClockUriCode(), true, context)
                },
                stringResource(R.string.fragment_clocking_check_in_button_text),
                buttonState
            )
            ClockingButton(
                {
                    initiateCall(clocking.getFullOffClockUriCode(), false, context)
                },
                stringResource(R.string.fragment_clocking_check_out_button_text),
                buttonState
            )
        }

    }
}

private fun initiateCall(uri: Uri, isOnClock: Boolean, context: Context){
    val dialIntent = Intent(Intent.ACTION_CALL)
    dialIntent.setData(uri)
    releaseLockingScreen()
    startLockingScreen(isOnClock)
    context.startActivity(dialIntent)
    println(uri)
}

private fun startLockingScreen(isOnClock: Boolean) {
    val test = clocking.calculateTotalWaitTime(isOnClock)
    Log.i("MainActivity","locking for $test seconds")
    wakeLock.acquire(clocking.calculateTotalWaitTime(isOnClock)*1000L)
}

private fun releaseLockingScreen() {
    Log.i("MainActivity", "releasing lock")
    if (wakeLock.isHeld) {
        wakeLock.release()
        Log.i("MainActivity", "lock released")
    }
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