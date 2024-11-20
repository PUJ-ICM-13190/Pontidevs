package com.example.emprendenow

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.emprendenow.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {
    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1
    }
    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: MessageAdapter
    private val messages = mutableListOf<Message>()
    private val database = FirebaseDatabase.getInstance().reference
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "" // ID del usuario actual
    private lateinit var chatId: String // ID del chat
    private lateinit var clienteId: String
    private lateinit var empresaId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clienteId = intent.getStringExtra("clienteId") ?: ""
        empresaId = intent.getStringExtra("empresaId") ?: ""
        chatId = "$clienteId-$empresaId" // Construcción del chatId único

        setupRecyclerView()
        listenForMessages()
        requestNotificationPermission()

        binding.btnSend.setOnClickListener {
            val content = binding.editMessage.text.toString().trim()
            if (content.isNotEmpty()) {
                sendMessage(content)
                binding.editMessage.text.clear()
            }
        }
    }


    private fun setupRecyclerView() {
        binding.recyclerMessages.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(messages, currentUserId)
        binding.recyclerMessages.adapter = adapter
    }

    private fun sendMessage(content: String) {
        val messageId = database.child("messages").child(chatId).push().key!! // Generar ID único para el mensaje
        val message = Message(
            id = messageId,
            senderId = currentUserId,
            content = content,
            timestamp = System.currentTimeMillis()
        )

        database.child("messages").child(chatId).child(messageId).setValue(message).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Enviar notificación al destinatario
                sendNotificationToRecipient(message)
            }
        }
    }

    private fun listenForMessages() {
        database.child("messages").child(chatId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()
                for (data in snapshot.children) {
                    val message = data.getValue(Message::class.java)
                    if (message != null) {
                        messages.add(message)
                    }
                }
                adapter.notifyDataSetChanged()
                binding.recyclerMessages.scrollToPosition(messages.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun sendNotificationToRecipient(message: Message) {
        val recipientId = if (message.senderId == clienteId) empresaId else clienteId // Determinar destinatario
        val notificationData = mapOf(
            "title" to "Nuevo Mensaje",
            "body" to message.content,
            "clienteId" to clienteId,
            "empresaId" to empresaId
        )

        // Guardar la notificación en Firebase (o enviar a través de un backend)
        FirebaseDatabase.getInstance().reference.child("notifications").child(recipientId).push().setValue(notificationData)
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Solo para Android 13+
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                Toast.makeText(this, "Permiso para notificaciones concedido", Toast.LENGTH_SHORT).show()
            } else {
                // Permiso denegado
                Toast.makeText(this, "Permiso para notificaciones denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }



    data class Message(
        val id: String = "",
        val senderId: String = "",
        val content: String = "",
        val timestamp: Long = 0L
    )
}


