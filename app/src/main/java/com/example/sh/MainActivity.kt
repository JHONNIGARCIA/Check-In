package com.example.sh

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sh.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // El permiso se ha solicitado. No se necesita acción aquí.
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == CHECKIN_ACTION) {
                reloadData()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Usar ViewBinding para inflar el nuevo layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        askNotificationPermission()
        reloadData()
    }

    override fun onResume() {
        super.onResume()
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RECEIVER_NOT_EXPORTED
        } else {
            0
        }
        registerReceiver(receiver, IntentFilter(CHECKIN_ACTION), flags)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun reloadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val lines = CheckinStore.readAll(this@MainActivity).reversed() // Muestra los más recientes primero
                withContext(Dispatchers.Main) {
                    // Conectar el RecyclerView con el Adapter
                    binding.recyclerView.adapter = CheckinAdapter(lines)
                }
            } catch (t: Throwable) {
                // Manejo de errores (opcional)
            }
        }
    }
}
