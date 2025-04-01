package com.example.generalattendance

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.provider.Settings
import android.util.Log


class RevertSettingService : Service() {
    private lateinit var callListener: CallListener

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("RevertSettingService", "Starting Service")
        val channelId = "return_channel"
        val channelName = "Return Service Channel"
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        channel.description = "Channel for RevertSettingService foreground notification"
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        // Build the notification
        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Attendance App")
            .setContentText("RevertSettingService")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Required field
            .build()

        val defaultScreenTimeout = intent?.getIntExtra("defaultScreenTimeout", 30000)
        callListener =
            CallListener(
                context = this,
                onCallStateIdle = {
                    Log.d("RevertSettingService", "Telephony Call Back Idle")
                    Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, defaultScreenTimeout!!)
                    Log.d("RevertSettingService", "Changed back timeout: ${Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)}")
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            )

        // Start the service in the foreground
        startForeground(1, notification)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d("RevertSettingService", "Service Destroying")
        callListener.unregister()
    }
}