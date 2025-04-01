package com.example.generalattendance

import android.content.Context
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager

class CallListener(private val context: Context, private val onCallStateIdle: () -> Unit = {}, private val onCallStateOffHook: () -> Unit = {}) {
    private lateinit var telephonyCallback: TelephonyCallback
    private lateinit var phoneStateListener: PhoneStateListener

    fun register(){
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyCallback = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                override fun onCallStateChanged(state: Int) {
                    when (state){
                        TelephonyManager.CALL_STATE_IDLE -> onCallStateIdle()
                        TelephonyManager.CALL_STATE_OFFHOOK -> onCallStateOffHook()
                        else -> {}
                    }
                }
            }
            telephonyManager.registerTelephonyCallback(context.mainExecutor, telephonyCallback)
        } else {
            phoneStateListener = object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                    when (state){
                        TelephonyManager.CALL_STATE_IDLE -> onCallStateIdle()
                        TelephonyManager.CALL_STATE_OFFHOOK -> onCallStateOffHook()
                        else -> {}
                    }
                }
            }
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    fun unregister(){
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyManager.unregisterTelephonyCallback(telephonyCallback)
        } else {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        }
    }
}