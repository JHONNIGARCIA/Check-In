package com.example.sh

import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import android.widget.Toast
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class AckListenerService : WearableListenerService() {
    override fun onMessageReceived(event: MessageEvent) {
        if (event.path == "/ack") {
            // Toast de confirmación
            Toast.makeText(this, "Check-in confirmado", Toast.LENGTH_SHORT).show()
            // Vibración corta
            val vib = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val effect = VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
            vib.vibrate(effect)
        }
    }
}
