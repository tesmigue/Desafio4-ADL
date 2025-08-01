package com.tesmigue.climaapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tesmigue.climaapp.adapter.PronosticoAdapter
import com.tesmigue.climaapp.databinding.ActivityPronosticoBinding
import com.tesmigue.climaapp.repository.ClimaRepository
import kotlinx.coroutines.launch

class PronosticoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPronosticoBinding
    private val repo = ClimaRepository()
    private lateinit var adapter: PronosticoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPronosticoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ciudad = intent.getStringExtra("ciudad") ?: "Santiago"
        adapter = PronosticoAdapter(emptyList())
        binding.rvPronostico.layoutManager = LinearLayoutManager(this)
        binding.rvPronostico.adapter = adapter

        lifecycleScope.launch {
            val pronostico = repo.obtenerPronostico(ciudad)
            adapter.actualizarLista(pronostico.list)
        }
    }
}