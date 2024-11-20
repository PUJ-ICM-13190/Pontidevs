package com.example.emprendenow

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.example.emprendenow.databinding.TarjetaProductoBinding
import com.google.firebase.storage.FirebaseStorage

class AdaptadorProducto(context: Context, private val productos: List<AgregarProductoActivity.Producto>): ArrayAdapter<AgregarProductoActivity.Producto>(context, 0 , productos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: TarjetaProductoBinding
        val view: View
        if(convertView == null) {
            binding = TarjetaProductoBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as TarjetaProductoBinding
            view = convertView
        }

        val producto = productos[position]
        binding.nombreProducto.text = producto.name
        binding.descripcion.text = producto.description
        binding.precio.text = producto.price.toString()

        val storageRef = FirebaseStorage.getInstance().reference
            .child("productos/${producto.name}.jpg")

        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                Glide.with(context)
                    .load(uri)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(binding.photo)
            }
            .addOnFailureListener { exception ->
                binding.photo.setImageResource(R.drawable.ic_launcher_foreground)
                Log.e("Storage", "Error al cargar imagen: ${exception.message}")
            }

        view.setOnClickListener {
            val intent = Intent(context, InfoEmprendimientoActivity::class.java)
            intent.putExtra("ProductoName", producto.name)
            context.startActivity(intent)
        }

        return view
    }

}