package com.example.emprendenow

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import  android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.emprendenow.databinding.TarjetaEmpresaBinding

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

            view.setOnClickListener {
                val intent = Intent(context, InfoEmprendimientoActivity::class.java)
                intent.putExtra("EmprendimientoName", empresa.name)
                context.startActivity(intent)
            }

            return view
        }
}