package com.example.sh

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService

const val CHECKIN_ACTION = "com.example.sh.NEW_CHECKIN"

class PhoneMessageListenerService : WearableListenerService() {

    override fun onMessageReceived(event: MessageEvent) {
        if (event.path == "/checkin") {
            // 1) Guarda registro local (timestamp, fuente)
            CheckinStore.appendNow(this, "wear")

            // 2) Avisa a la app que hay un nuevo registro
            sendBroadcast(Intent(CHECKIN_ACTION))

            // 3) Notificación al usuario
            notifyPhone("Check-in desde el reloj", "¡Recibido y guardado!")

            // 4) Responde con ACK al reloj
            val nodeClient = Wearable.getNodeClient(this)
            val msgClient  = Wearable.getMessageClient(this)
            nodeClient.connectedNodes.addOnSuccessListener { nodes ->
                nodes.forEach { node ->
                    msgClient.sendMessage(node.id, "/ack", "OK".toByteArray())
                }
            }
        }
    }

    private fun notifyPhone(title: String, text: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "appwear_notifications"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                NotificationChannel(channelId, "AppWear", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        val n = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(title)
            .setContentText(text)
            .build()
        nm.notify(1001, n)
    }
}
