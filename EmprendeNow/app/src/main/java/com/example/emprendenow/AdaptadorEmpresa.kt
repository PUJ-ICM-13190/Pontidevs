package com.example.emprendenow

import android.content.Context
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
            binding.nombreEmpresa.text = empresa.name

            return view
        }
}