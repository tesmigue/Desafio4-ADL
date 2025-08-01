package com.tesmigue.climaapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tesmigue.climaapp.adapter.CiudadAdapter
import com.tesmigue.climaapp.data.AppDatabase
import com.tesmigue.climaapp.databinding.ActivityMainBinding
import com.tesmigue.climaapp.model.Ciudad
import com.tesmigue.climaapp.repository.CiudadRepository
import com.tesmigue.climaapp.repository.ClimaRepository
import com.tesmigue.climaapp.utils.LocationHelper
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var climaRepo: ClimaRepository
    private lateinit var ciudadRepo: CiudadRepository
    private lateinit var ciudadAdapter: CiudadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ciudadDao = AppDatabase.getDatabase(this).ciudadDao()
        climaRepo = ClimaRepository()
        ciudadRepo = CiudadRepository(ciudadDao)

        setupListeners()
        setupRecyclerView()
    }

    private fun setupListeners() {
        binding.btnBuscar.setOnClickListener {
            val ciudad = binding.etCiudad.text.toString().trim()
            if (ciudad.isNotEmpty()) {
                buscarClima(ciudad)
                guardarCiudad(ciudad)
            } else {
                Toast.makeText(this, "Ingresa una ciudad", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnPronostico.setOnClickListener {
            val ciudad = binding.etCiudad.text.toString().trim()
            if (ciudad.isNotEmpty()) {
                val intent = Intent(this, PronosticoActivity::class.java)
                intent.putExtra("ciudad", ciudad)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Primero ingresa o busca una ciudad", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnUbicacion.setOnClickListener {
            solicitarPermisosUbicacion()
        }
    }

    private fun putExtra(s: String, ciudad: String) {

    }

    private fun buscarClima(ciudad: String) {
        lifecycleScope.launch {
            try {
                val clima = climaRepo.obtenerClima(ciudad)
                binding.tvCiudad.text = "🏙️ ${clima.nombre}"
                binding.tvTemperatura.text = "🌡️ ${clima.main.temp.toInt()}°C"
                binding.tvDescripcion.text = clima.weather[0].description.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarCiudad(nombreCiudad: String) {
        lifecycleScope.launch {
            ciudadRepo.insertarCiudad(nombreCiudad)
        }
    }

    private fun setupRecyclerView() {
        binding.rvCiudades.layoutManager = LinearLayoutManager(this)
        ciudadRepo.obtenerCiudadesFavoritas().asLiveData().observe(this) { lista ->
            val adaptadas = lista.map { Ciudad(it.nombre) }
            ciudadAdapter = CiudadAdapter(adaptadas) { ciudad ->
                binding.etCiudad.setText(ciudad.nombre)
                buscarClima(ciudad.nombre)
            }
            binding.rvCiudades.adapter = ciudadAdapter
        }
    }

    private fun solicitarPermisosUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            obtenerUbicacion()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                obtenerUbicacion()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }

    private fun obtenerUbicacion() {
        LocationHelper.obtenerUbicacion(this) { location ->
            if (location != null) {
                lifecycleScope.launch {
                    try {
                        val clima = climaRepo.obtenerClimaPorCoordenadas(
                            location.latitude,
                            location.longitude
                        )
                        binding.tvCiudad.text = "📍 ${clima.nombre}"
                        binding.tvTemperatura.text = "🌡️ ${clima.main.temp.toInt()}°C"
                        binding.tvDescripcion.text = clima.weather[0].description
                        guardarCiudad(clima.nombre)
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "No se pudo obtener ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
