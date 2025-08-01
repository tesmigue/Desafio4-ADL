package com.tesmigue.climaapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tesmigue.climaapp.R
import com.tesmigue.climaapp.model.DiaPronostico
import java.text.SimpleDateFormat
import java.util.Locale

class PronosticoAdapter(
    private var pronosticos: List<DiaPronostico> = emptyList()
) : RecyclerView.Adapter<PronosticoAdapter.PronosticoViewHolder>() {


    fun actualizarLista(nuevaLista: List<DiaPronostico>) {
        pronosticos = nuevaLista
        notifyDataSetChanged()
    }

    class PronosticoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcono: ImageView = itemView.findViewById(R.id.ivIconoClima)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionPronostico)
        val tvTemperatura: TextView = itemView.findViewById(R.id.tvTemperaturaPronostico)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PronosticoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dia_pronostico, parent, false)
        return PronosticoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PronosticoViewHolder, position: Int) {
        val pronostico = pronosticos[position]

        try {

            holder.tvFecha.text = formatearFecha(pronostico.dt_txt)


            holder.tvDescripcion.text = pronostico.weather.firstOrNull()?.description?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            } ?: "Sin descripción"


            holder.tvTemperatura.text = "${pronostico.main.temp.toInt()}°C"


            val main = pronostico.weather.firstOrNull()?.main ?: "Clear"
            holder.ivIcono.contentDescription = obtenerEmojiClima(main)

        } catch (e: Exception) {
            holder.tvFecha.text = "Error"
            holder.tvDescripcion.text = "No disponible"
            holder.tvTemperatura.text = "--°C"
        }
    }

    override fun getItemCount(): Int = pronosticos.size

    private fun formatearFecha(fechaString: String): String {
        return try {
            val formatoEntrada = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val fecha = formatoEntrada.parse(fechaString)
            val formatoSalida = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatoSalida.format(fecha!!)
        } catch (e: Exception) {
            fechaString.substring(0, 10)
        }
    }

    private fun obtenerEmojiClima(condicion: String): String {
        return when (condicion.lowercase()) {
            "clear" -> "☀️"
            "clouds" -> "☁️"
            "rain" -> "🌧️"
            "drizzle" -> "🌦️"
            "thunderstorm" -> "⛈️"
            "snow" -> "❄️"
            "mist", "fog" -> "🌫️"
            else -> "🌤️"
        }
    }
}
