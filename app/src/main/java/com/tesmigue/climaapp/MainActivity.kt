package com.tesmigue.climaapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tesmigue.climaapp.PronosticoActivity
import com.tesmigue.climaapp.databinding.ActivityMainBinding
import com.tesmigue.climaapp.repository.ClimaRepository
import com.tesmigue.climaapp.utils.LocationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var climaRepository: ClimaRepository
    private val PERMISSIONS_REQUEST_LOCATION = 100
    private var ultimaCiudadConsultada = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        climaRepository = ClimaRepository()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBuscar.setOnClickListener {
            val ciudad = binding.etCiudad.text.toString().trim()
            if (ciudad.isNotBlank()) {
                obtenerClima(ciudad)
            } else {
                Toast.makeText(this, "Por favor, ingrese el nombre de una ciudad", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnUbicacion.setOnClickListener {
            solicitarPermisosUbicacion()
        }

        // Click para abrir pronóstico
        binding.tvCiudad.setOnClickListener {
            if (ultimaCiudadConsultada.isNotEmpty()) {
                abrirPronostico(ultimaCiudadConsultada)
            } else {
                Toast.makeText(this, "Primero busque el clima de una ciudad", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerClima(ciudad: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Mostrar loading
                binding.tvCiudad.text = "🔄 Cargando..."
                binding.tvTemperatura.text = "--°C"
                binding.tvDescripcion.text = "Obteniendo datos del clima..."

                val climaResponse = climaRepository.obtenerClima(ciudad)

                // Actualizar UI con datos obtenidos
                binding.tvCiudad.text = "🏙️ ${climaResponse.nombre}"
                binding.tvDescripcion.text = "☁️ ${climaResponse.weather[0].description.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }}"
                binding.tvTemperatura.text = "${climaResponse.main.temp.toInt()}°C"
                ultimaCiudadConsultada = climaResponse.nombre

                Toast.makeText(this@MainActivity, "Clima actualizado", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                // Manejar errores
                binding.tvCiudad.text = "❌ Error"
                binding.tvTemperatura.text = "--°C"
                binding.tvDescripcion.text = "No se pudo obtener el clima"

                val errorMessage = when {
                    e.message?.contains("Sin conexión") == true -> "Sin conexión a internet"
                    e.message?.contains("404") == true -> "Ciudad no encontrada"
                    e.message?.contains("401") == true -> "Error de autenticación API"
                    else -> "Error al obtener el clima: ${e.message}"
                }

                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun abrirPronostico(ciudad: String) {
        val intent = Intent(this, PronosticoActivity::class.java)
        intent.putExtra("CIUDAD_NOMBRE", ciudad)
        startActivity(intent)
    }

    private fun solicitarPermisosUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            obtenerUbicacion()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_LOCATION &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion()
        } else {
            Toast.makeText(
                this,
                "Permiso de ubicación requerido para obtener clima actual",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun obtenerUbicacion() {
        binding.tvCiudad.text = "📍 Obteniendo ubicación..."
        binding.tvTemperatura.text = "--°C"
        binding.tvDescripcion.text = "Buscando su ubicación actual..."

        LocationHelper.obtenerUbicacion(this) { location ->
            if (location != null) {
                val geocoder = Geocoder(this, Locale.getDefault())
                try {
                    val direcciones = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val ciudad = direcciones?.firstOrNull()?.locality ?: "Ciudad no encontrada"

                    if (ciudad != "Ciudad no encontrada") {
                        binding.etCiudad.setText(ciudad)
                        obtenerClima(ciudad)
                    } else {
                        mostrarErrorUbicacion("No se pudo determinar la ciudad")
                    }
                } catch (e: Exception) {
                    mostrarErrorUbicacion("Error al obtener nombre de la ciudad")
                }
            } else {
                mostrarErrorUbicacion("No se pudo obtener ubicación")
            }
        }
    }

    private fun mostrarErrorUbicacion(mensaje: String) {
        binding.tvCiudad.text = "❌ Error de ubicación"
        binding.tvTemperatura.text = "--°C"
        binding.tvDescripcion.text = "Busca una ciudad manualmente"
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}