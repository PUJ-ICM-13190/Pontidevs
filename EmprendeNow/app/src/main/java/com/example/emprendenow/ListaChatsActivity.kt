package com.example.emprendenow

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emprendenow.databinding.ActivityListaChatsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ListaChatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaChatsBinding
    private val database = FirebaseDatabase.getInstance().reference.child("users")
    private val empresas = mutableListOf<User>()
    private lateinit var adapter: ChatAdapter
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ChatAdapter(empresas) { empresa ->
            crearChat(empresa)
        }
        binding.recyclerChats.layoutManager = LinearLayoutManager(this)
        binding.recyclerChats.adapter = adapter

        cargarEmpresas()
    }

    private fun cargarEmpresas() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                empresas.clear()
                for (data in snapshot.children) {
                    val usuario = data.getValue(User::class.java)
                    if (usuario != null && usuario.userType == "Empresa") {
                        // Usar la clave del nodo como `id`
                        val id = data.key ?: ""
                        val empresa = usuario.copy(id = id)
                        empresas.add(empresa)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListaChatsActivity, "Error al cargar empresas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun crearChat(empresa: User) {
        val clienteId = FirebaseAuth.getInstance().currentUser?.uid ?: "" // ID del cliente autenticado
        val empresaId = empresa.id // ID de la empresa seleccionada

        if (empresaId.isEmpty()) {
            Toast.makeText(this, "Error: ID de la empresa no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        // Referencia al nodo correcto en Firebase
        val chatNode = FirebaseDatabase.getInstance().reference.child("chats").child(clienteId).child(empresaId)

        // Datos que se deben guardar
        val chatData = mapOf(
            "lastMessage" to "", // Mensaje inicial vacío
            "timestamp" to System.currentTimeMillis() // Marca de tiempo actual
        )

        // Guardar los datos del chat en Firebase
        chatNode.setValue(chatData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Si el chat se guarda correctamente, abrir la actividad del chat
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("clienteId", clienteId)
                intent.putExtra("empresaId", empresaId)
                startActivity(intent)
            } else {
                // Mostrar un mensaje de error si ocurre un fallo
                Toast.makeText(this, "Error al crear el chat", Toast.LENGTH_SHORT).show()
            }
        }
    }
}