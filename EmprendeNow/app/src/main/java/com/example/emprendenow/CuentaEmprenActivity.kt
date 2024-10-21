package com.example.emprendenow

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.emprendenow.databinding.ActivityCuentaEmprenBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

class CuentaEmprenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCuentaEmprenBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuentaEmprenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navbar = binding.bottomNavigation
        val name = binding.textName
        val mail = binding.textMail
        val sign_out = binding.signOut
        val userId = intent.getStringExtra("user")

        if (userId != null) {
            getUserData(userId)
        }

        mAuth = Firebase.auth
        sign_out.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, CrearCuentaActivity::class.java)
            startActivity(intent)
        }

        navbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val user = mAuth.currentUser
                    val intent = Intent(this, MenuEmprendedorActivity::class.java)
                    intent.putExtra("user", user!!.uid)
                    startActivity(intent)
                    true
                }
                R.id.chat -> {
                    true
                }
                R.id.account -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun getUserData(userId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users/$userId")

        databaseReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userSnapshot = task.result
                if (userSnapshot != null && userSnapshot.exists()) {
                    val name = userSnapshot.child("name").value.toString()
                    val email = userSnapshot.child("email").value.toString()

                    binding.textName.text = name
                    binding.textMail.text = email
                }
            }
        }
    }
}