package com.example.emprendenow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    private val empresas: List<User>,
    private val onClick: (User) -> Unit
) : RecyclerView.Adapter<ChatAdapter.EmpresaViewHolder>() {

    class EmpresaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val email: TextView = view.findViewById(R.id.txtEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpresaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_chat_adapter, parent, false)
        return EmpresaViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmpresaViewHolder, position: Int) {
        val empresa = empresas[position]
        holder.name.text = empresa.name
        holder.email.text = empresa.email
        holder.itemView.setOnClickListener { onClick(empresa) }
    }

    override fun getItemCount(): Int = empresas.size
}

