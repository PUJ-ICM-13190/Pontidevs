package com.example.emprendenow

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emprendenow.databinding.ActivityListaChatsEmprendedorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ListaChatsEmprendedor : AppCompatActivity() {

    private lateinit var binding: ActivityListaChatsEmprendedorBinding
    private val database = FirebaseDatabase.getInstance().reference.child("chats")
    private val chats = mutableListOf<Chat>()
    private lateinit var adapter: ChatEmprendedorAdapter
    private val empresaId = FirebaseAuth.getInstance().currentUser?.uid ?: "" // ID del emprendedor autenticado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaChatsEmprendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ChatEmprendedorAdapter(chats) { chat ->
            abrirChat(chat)
        }
        binding.recyclerChatsEmprendedor.layoutManager = LinearLayoutManager(this)
        binding.recyclerChatsEmprendedor.adapter = adapter

        cargarChats()
    }

    private fun cargarChats() {
        val usersDatabase = FirebaseDatabase.getInstance().reference.child("users")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chats.clear()
                for (clienteSnapshot in snapshot.children) {
                    for (empresaSnapshot in clienteSnapshot.children) {
                        if (empresaSnapshot.key == empresaId) {
                            val clienteId = clienteSnapshot.key ?: continue
                            val chat = empresaSnapshot.getValue(Chat::class.java)
                            if (chat != null) {
                                usersDatabase.child(clienteId).addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(userSnapshot: DataSnapshot) {
                                        val clienteName = userSnapshot.child("name").value as? String ?: "Desconocido"
                                        chats.add(chat.copy(clienteId = clienteId, clienteName = clienteName))
                                        adapter.notifyDataSetChanged()
                                    }
                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(
                                            this@ListaChatsEmprendedor,
                                            "Error al cargar nombres de clientes",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListaChatsEmprendedor, "Error al cargar chats", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun abrirChat(chat: Chat) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("clienteId", chat.clienteId)
        intent.putExtra("empresaId", empresaId)
        startActivity(intent)
    }

    data class Chat(
        val clienteId: String = "",
        val clienteName: String = "",
        val lastMessage: String = "",
        val timestamp: Long = 0L
    )
}
