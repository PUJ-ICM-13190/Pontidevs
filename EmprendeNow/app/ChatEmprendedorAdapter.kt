package com.example.emprendenow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import com.example.emprendenow.ListaChatsEmprendedor.Chat

class ChatEmprendedorAdapter(
    private val chats: List<Chat>,
    private val onClick: (Chat) -> Unit
) : RecyclerView.Adapter<ChatEmprendedorAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clienteName: TextView = view.findViewById(R.id.txtClienteName)
        val lastMessage: TextView = view.findViewById(R.id.txtLastMessage)
        val timestamp: TextView = view.findViewById(R.id.txtTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_chat_emprendedor_adapter, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]
        holder.clienteName.text = chat.clienteName // Mostrar el nombre del cliente
        holder.lastMessage.text = chat.lastMessage
        holder.timestamp.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(chat.timestamp))
        holder.itemView.setOnClickListener { onClick(chat) }
    }

    override fun getItemCount(): Int = chats.size
}

