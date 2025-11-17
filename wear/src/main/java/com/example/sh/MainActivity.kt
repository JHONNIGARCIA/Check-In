package com.example.sh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 1. Envolver todo en el tema de Material para Wear OS
            // Esto automáticamente pondrá el fondo oscuro y el texto claro.
            MaterialTheme {
                Screen()
            }
        }
    }

    @Composable
    private fun Screen() {
        var isLoading by remember { mutableStateOf(false) }
        var status by remember { mutableStateOf("Listo para check-in") }
        val scope = remember { lifecycleScope }

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(status) // El color se adaptará automáticamente al tema
                    Spacer(Modifier.height(12.dp))
                    // 2. Usar un Chip en lugar de un Button para un look más nativo de Wear OS
                    Chip(
                        label = { Text("Hacer Check-in") },
                        icon = { Icon(imageVector = Icons.Default.Check, contentDescription = "Check") },
                        onClick = {
                            isLoading = true
                            status = "Enviando…"
                            scope.launch {
                                sendCheckIn { ok ->
                                    status = if (ok) "Check-in enviado. Esperando ACK…" else "No se pudo enviar"
                                    isLoading = false
                                }
                            }
                        },
                        colors = ChipDefaults.primaryChipColors()
                    )
                }
            }
        }
    }

    private suspend fun sendCheckIn(cb: (Boolean) -> Unit) {
        val nodes = Wearable.getNodeClient(this).connectedNodes.await()
        if (nodes.isEmpty()) {
            cb(false)
            return
        }
        try {
            val mc = Wearable.getMessageClient(this)
            nodes.forEach { mc.sendMessage(it.id, "/checkin", "hello".toByteArray()).await() }
            cb(true)
        } catch (_: Exception) {
            cb(false)
        }
    }
}
