package com.tesmigue.climaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tesmigue.climaapp.R
import com.tesmigue.climaapp.model.Ciudad

class CiudadAdapter(
    private val ciudades: List<Ciudad>,
    private val onClick: (Ciudad) -> Unit
) : RecyclerView.Adapter<CiudadAdapter.CiudadViewHolder>() {

    inner class CiudadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvCiudadNombre)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CiudadViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ciudad, parent, false)
        return CiudadViewHolder(view)
    }

    override fun getItemCount(): Int = ciudades.size

    override fun onBindViewHolder(holder: CiudadViewHolder, position: Int) {
        val ciudad = ciudades[position]
        holder.tvNombre.text = ciudad.nombre
        holder.itemView.setOnClickListener { onClick(ciudad) }
    }
}
