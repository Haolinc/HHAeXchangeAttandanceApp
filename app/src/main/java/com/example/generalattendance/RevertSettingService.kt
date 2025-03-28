package com.example.generalattendance

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log


class RevertSettingService : Service() {
    private lateinit var telephonyCallback: TelephonyCallback
    private lateinit var phoneStateListener: PhoneStateListener

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("Starting service")
        val channelId = "return_channel"
        val channelName = "Return Service Channel"
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        channel.description = "Channel for ReturnService foreground notification"
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        // Build the notification
        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Attendance App")
            .setContentText("Waiting to return")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Required field
            .build()

        val defaultScreenTimeout = intent?.getIntExtra("defaultScreenTimeout", 30000)
        setupCallStateListener(this, defaultScreenTimeout!!)

        // Start the service in the foreground
        startForeground(1, notification)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        println("service dying")
        val telephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyManager.unregisterTelephonyCallback(telephonyCallback)
        } else {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        }

    }

    private fun setupCallStateListener(context: Context, defaultScreenTimeout: Int) {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyCallback = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                override fun onCallStateChanged(state: Int) {
                    if (state == TelephonyManager.CALL_STATE_IDLE){
                        Log.d("CallState", "Telephony Call Back Idle")
                        Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, defaultScreenTimeout)
                        Log.d("CallState", "Changed back timeout: ${Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)}")
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }
                }
            }
            telephonyManager.registerTelephonyCallback(context.mainExecutor, telephonyCallback)
        } else {
            phoneStateListener = object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                    if (state == TelephonyManager.CALL_STATE_IDLE){
                        Log.d("CallState", "Phone Listen Idle")
                        Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, defaultScreenTimeout)
                        Log.d("CallState", "Changed back timeout: ${Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)}")
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }
                }
            }
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

}