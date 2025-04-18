package com.example.generalattendance.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.generalattendance.CallListener
import com.example.generalattendance.Clocking
import com.example.generalattendance.R
import com.example.generalattendance.RevertSettingService
import com.example.generalattendance.permission.CallPermissionChecker
import com.example.generalattendance.permission.PermissionHelper
import com.example.generalattendance.viewmodels.EmployeeInfoViewModel
import com.example.generalattendance.viewmodels.UIViewModel

private const val LOG_TAG = "Clocking"

@Composable
fun ClockingFragment(employeeInfoViewModel: EmployeeInfoViewModel, uiViewModel: UIViewModel){
    val context = LocalContext.current
    var isCallStateIdle by remember{ mutableStateOf(false) }
    var isCallPermissionGranted by remember { mutableStateOf(CallPermissionChecker().hasPermission(context)) }
    val isFirstTime by uiViewModel.getIsFirstTime().observeAsState(true)
    val callListener = remember {
        CallListener(
            context = context,
            onCallStateIdleFunction = {isCallStateIdle = true; Log.i(LOG_TAG, "isCallStateIdle to true")},
            onCallStateOffHookFunction = {isCallStateIdle = false; Log.i(LOG_TAG, "isCallStateIdle to false")},
            onCallStateRingingFunction = {isCallStateIdle = false; Log.i(LOG_TAG, "isCallStateIdle to false")}
        )
    }
    // Fetch permission if not granted
    LaunchedEffect(Unit) {
        if (!isCallPermissionGranted){
            PermissionHelper.requestCallPermission {
                isCallPermissionGranted = it
            }
        }
        if (isFirstTime){
            uiViewModel.setIsFirstTime(false)
        }
    }
    if (isCallPermissionGranted) {
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> {
                        Log.i(LOG_TAG, "Unregister at onPause")
                        callListener.unregister()
                    }

                    Lifecycle.Event.ON_RESUME -> {
                        Log.i(LOG_TAG, "Register at onResume")
                        callListener.register()
                    }
                    else -> Unit
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                Log.i(LOG_TAG, "Remove observer and unregister call listener at onDispose")
                lifecycleOwner.lifecycle.removeObserver(observer)
                callListener.unregister()
            }
        }
    }

    val workNumList by employeeInfoViewModel.getWorkNumList().observeAsState(emptyList())
    val dialNumber by employeeInfoViewModel.getDialNum().observeAsState("")
    val employeeNumber by employeeInfoViewModel.getEmployeeNum().observeAsState("")
    val buttonState by remember (workNumList, employeeNumber, dialNumber, isCallPermissionGranted, isCallStateIdle) {
        derivedStateOf {
            workNumList.isNotEmpty() &&
                    employeeNumber.length == 6 &&
                    dialNumber.length == 10 &&
                    isCallPermissionGranted &&
                    isCallStateIdle
        }
    }

    // Split into 2 sections, Info Top Buttons Bottom
    Column(
        modifier = Modifier.fillMaxSize()
    ){
        // ----Info Top----
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1.25f)
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 10.dp)
        ){
            val emptyDefaultText = stringResource(R.string.fragment_clocking_content_text_default)
            // ----Employee Number, Dial Number Section----
            // Split into 2 sections top Employee Number and Dial Number, bottom Work numbers
            Column(
                modifier = Modifier.weight(1.25f)
            ){
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                        .fillMaxWidth()
                ){
                    // ----Employee Number----
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            TopSectionTitleText(
                                content = stringResource(R.string.fragment_clocking_header_employee_number),
                                modifier = Modifier.weight(1f)
                            )
                            val employeeNumText =
                                if (employeeNumber == "") emptyDefaultText else employeeNumber
                            EmployeeInfoSection(content = employeeNumText, modifier = Modifier.weight(1f))
                            val employeeNumErrorText =
                                if (employeeNumber.length < 6)
                                    stringResource(R.string.error_text_employee_number)
                                else
                                    ""
                            ErrorText(employeeNumErrorText, Modifier.weight(1f))
                        }
                    }

                    // ----Dial Number----
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    ){
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            TopSectionTitleText(
                                content = stringResource(R.string.fragment_clocking_header_dial_number),
                                modifier = Modifier.weight(1f)
                            )
                            val dialNumText = if (dialNumber == "") emptyDefaultText else dialNumber
                            EmployeeInfoSection(content = dialNumText, modifier = Modifier.weight(1f))
                            val dialNumErrorText =
                                if (dialNumber.length < 10)
                                    stringResource(R.string.error_text_dial_number)
                                else
                                    ""
                            ErrorText(dialNumErrorText, Modifier.weight(1f))
                        }
                    }

                }
            }

            // ----Work Number----
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1.7f)
            ){
                TopSectionTitleText(content = stringResource(R.string.fragment_clocking_header_work_number), modifier = Modifier)
                Spacer(modifier = Modifier.height(5.dp))
                val workNumText = if (workNumList.isEmpty()) emptyDefaultText else workNumList.sorted().joinToString()
                EmployeeInfoSection(content = workNumText, modifier = Modifier)
                // to save some more space
                if (workNumList.isEmpty())
                    ErrorText(stringResource(R.string.error_text_work_number), Modifier)
            }

        }

        // ----Buttons----
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(0.75f)
        ){
            if (calculateTotalWaitTime(false, workNumList.size)*1000 > getSystemScreenTimeout(context) && !Settings.System.canWrite(context))
                ErrorText(text = stringResource(R.string.error_text_work_number_greater_than_screen_timeout), Modifier)
            if (!isCallPermissionGranted)
                ErrorText(text = stringResource(R.string.error_text_call_permission), Modifier)

            ClockingButton(
                {
                    val clocking = Clocking(employeeNumber, dialNumber, workNumList.sorted())
                    initiateCall(clocking.getFullOnClockUriCode(), true, context, workNumList.size)
                },
                stringResource(R.string.fragment_clocking_check_in_button_text),
                buttonState
            )
            ClockingButton(
                {
                    val clocking = Clocking(employeeNumber, dialNumber, workNumList.sorted())
                    initiateCall(clocking.getFullOffClockUriCode(), false, context, workNumList.size)
                },
                stringResource(R.string.fragment_clocking_check_out_button_text),
                buttonState
            )
        }

    }
}

private fun initiateCall(uri: Uri, isOnClock: Boolean, context: Context, workNumListSize: Int){
    val dialIntent = Intent(Intent.ACTION_CALL)
    dialIntent.setData(uri)
    context.startActivity(dialIntent)
    println(uri)

    if (Settings.System.canWrite(context)){
        Handler(Looper.getMainLooper()).postDelayed({
            // Revert after the call ended
            val serviceIntent = Intent(context, RevertSettingService::class.java)
            serviceIntent.putExtra("defaultScreenTimeout", getSystemScreenTimeout(context))
            context.startForegroundService(serviceIntent)

            println("overwriting system default")
            // Set system screen timeout
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, calculateTotalWaitTime(isOnClock, workNumListSize) * 1000)
            println("after overwrite the system timeout become ${getSystemScreenTimeout(context)}")
        }, 2000)
    }
}

private fun calculateTotalWaitTime(onClock: Boolean = true, workNumListSize: Int): Int {
    val initialWaitTime = 38
    if (onClock)
        return initialWaitTime
    return initialWaitTime + (workNumListSize + 1) * 8   // add 1 for 000 number set
}

private fun getSystemScreenTimeout(context: Context): Int{
    return Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
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
fun EmployeeInfoSection(content: String, modifier: Modifier){
    // Need to wrap the box
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ){
        Box(
            modifier = Modifier.border(2.dp, Color.Blue).padding(horizontal = 8.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ){
            Text(text = content, fontSize = 20.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun TopSectionTitleText(content: String, modifier: Modifier){
    Text(
        text = content,
        fontSize = 25.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        modifier = modifier.wrapContentHeight()
    )
}

@Composable
fun ErrorText(text: String, modifier: Modifier){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ){
        Text(text = text, fontSize = 15.sp, color = Color.Red, textAlign = TextAlign.Center, modifier = modifier.padding(5.dp))
    }

}