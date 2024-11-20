package com.example.emprendenow

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import  android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.example.emprendenow.databinding.TarjetaEmpresaBinding
import com.google.firebase.storage.FirebaseStorage

class AdaptadorEmpresa(context: Context, private val empresas: List<ListaEmpresasActivity.Empresa>): ArrayAdapter<ListaEmpresasActivity.Empresa>(context, 0 , empresas) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val binding: TarjetaEmpresaBinding
            val view: View
            if(convertView == null) {
                binding = TarjetaEmpresaBinding.inflate(LayoutInflater.from(context), parent, false)
                view = binding.root
                view.tag = binding
            } else {
                binding = convertView.tag as TarjetaEmpresaBinding
                view = convertView
            }

            val empresa = empresas[position]
            binding.nombreEmpresa.text = empresa.name
            binding.descripcion.text = empresa.descripcion

            val storageRef = FirebaseStorage.getInstance().reference
                .child("empresas/${empresa.name}.jpg")

            storageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    Glide.with(context)
                        .load(uri)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(binding.logo)
                }
                .addOnFailureListener { exception ->
                    binding.logo.setImageResource(R.drawable.ic_launcher_foreground)
                    Log.e("Storage", "Error al cargar imagen: ${exception.message}")
                }

            view.setOnClickListener {
                val intent = Intent(context, InfoEmprendimientoActivity::class.java)
                intent.putExtra("EmprendimientoName", empresa.name)
                context.startActivity(intent)
            }

            return view
        }
}