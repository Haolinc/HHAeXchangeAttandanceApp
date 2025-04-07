package com.example.generalattendance

import android.content.Context
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import org.jetbrains.annotations.VisibleForTesting

private const val LOG_TAG = "Call Listener"

class CallListener(
    private val context: Context,
    private val onCallStateIdleFunction: () -> Unit = {},
    private val onCallStateOffHookFunction: () -> Unit = {},
    private val onCallStateRingingFunction: () -> Unit = {}
) {
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    @VisibleForTesting
    internal var telephonyCallback: TelephonyCallback? = null
    @VisibleForTesting
    internal var phoneStateListener: PhoneStateListener? = null
    private var isRegistered = false

    fun register(){
        if (!isRegistered) {
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
        if (isRegistered) {
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
            TelephonyManager.CALL_STATE_IDLE -> onCallStateIdleFunction()
            TelephonyManager.CALL_STATE_OFFHOOK -> onCallStateOffHookFunction()
            TelephonyManager.CALL_STATE_RINGING -> onCallStateRingingFunction()
            else -> {}
        }
    }
}