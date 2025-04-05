package com.example.generalattendance

import android.content.Context
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import com.example.generalattendance.permission.CallPermissionChecker
import com.example.generalattendance.permission.PermissionChecker

private const val LOG_TAG = "Call Listener"

class CallListener(
    private val context: Context,
    private val onCallStateIdle: () -> Unit = {},
    private val onCallStateOffHook: () -> Unit = {},
    private val onCallStateRinging: () -> Unit = {}
) {
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    private var telephonyCallback: TelephonyCallback? = null
    private var phoneStateListener: PhoneStateListener? = null
    private var isRegistered = false
    private val callPermissionChecker: PermissionChecker = CallPermissionChecker()

    fun register(){
        if (!isRegistered && callPermissionChecker.hasPermission(context)) {
            Log.i(LOG_TAG, "call listener registering")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                telephonyCallback =
                    object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                        override fun onCallStateChanged(state: Int) {
                            processCallStateFunction(state)
                        }
                    }
                telephonyManager.registerTelephonyCallback(
                    context.mainExecutor,
                    telephonyCallback!!
                )
                Log.i(LOG_TAG, "SDK >= 31 call listener registered")
            } else {
                phoneStateListener = object : PhoneStateListener() {
                    @Deprecated("Deprecated in Java")
                    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                        processCallStateFunction(state)
                    }
                }
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
                Log.i(LOG_TAG, "SDK < 31 call listener registered")
            }
        }
        isRegistered = true
    }

    fun unregister(){
        if (isRegistered && callPermissionChecker.hasPermission(context)) {
            Log.i(LOG_TAG, "call listener unregistered")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                telephonyCallback?.let {
                    telephonyManager.unregisterTelephonyCallback(it)
                    telephonyCallback = null
                    Log.i(LOG_TAG, "SDK >= 31 call listener unregistered")
                }
            } else {
                phoneStateListener?.let {
                    telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
                    phoneStateListener = null
                    Log.i(LOG_TAG, "SDK < 31 call listener unregistered")
                }
            }
        }
        isRegistered = false
    }

    private fun processCallStateFunction(state: Int){
        when (state){
            TelephonyManager.CALL_STATE_IDLE -> onCallStateIdle()
            TelephonyManager.CALL_STATE_OFFHOOK -> onCallStateOffHook()
            TelephonyManager.CALL_STATE_RINGING -> onCallStateRinging()
            else -> {}
        }
    }
}