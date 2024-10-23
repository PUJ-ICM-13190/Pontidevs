package com.example.emprendenow

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Matcher
import java.util.regex.Pattern
import com.example.emprendenow.databinding.ActivityCrearCuentaBinding
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.Executor

class CrearCuentaActivity : AppCompatActivity() {

    private val TAG = CrearCuentaActivity::class.java.name
    private val VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

    lateinit var emailEdit: EditText
    lateinit var passEdit: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityCrearCuentaBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailEdit = binding.mail
        passEdit = binding.password
        val userTypeSpinner = binding.userType
        val userTypes = resources.getStringArray(R.array.tipos_usuarios)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, userTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userTypeSpinner.adapter = adapter
        val name = binding.name

        // Initialize Firebase Auth
        mAuth = Firebase.auth

        // Configuración de BiometricPrompt
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                // Error en la autenticación biométrica
                Toast.makeText(applicationContext, "Error de autenticación: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                // Autenticación biométrica exitosa
                handleLoginSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Falla en la autenticación biométrica
                Toast.makeText(applicationContext, "Autenticación fallida", Toast.LENGTH_SHORT).show()
            }
        })

        // Configuración de la información del prompt
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación Biométrica")
            .setSubtitle("Usa tu huella digital para autenticarte")
            .setNegativeButtonText("Cancelar")
            .build()

        // Botón para la autenticación biométrica
        binding.btnBiometricLogin.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.btnRegister.setOnClickListener {
            signUp()
        }

        binding.btnPass.setOnClickListener {
            forgotPassword()
        }

    }


    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }


    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val userId = currentUser.uid
            val databaseReference = FirebaseDatabase.getInstance().getReference("users/$userId")
            databaseReference.child("userType").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userType = task.result?.value.toString()
                    when (userType) {
                        "Cliente" -> {
                            val intent = Intent(baseContext, CuentaClienActivity::class.java)
                            intent.putExtra("user", currentUser.uid)
                            startActivity(intent)
                        }
                        "Empresa" -> {
                            val intent = Intent(this, CuentaEmprenActivity::class.java)
                            intent.putExtra("user", currentUser.uid)
                            startActivity(intent)
                        }
                    }
                } else {
                    Log.e(TAG, "Error getting user type: ${task.exception}")
                }
            }
        } else {
            emailEdit.setText("")
            passEdit.setText("")
        }
    }


    private fun validateForm(): Boolean {
        var valid = true
        val email = emailEdit.text.toString()
        if (TextUtils.isEmpty(email)) {
            emailEdit.error = "Required"
            valid = false
        } else {
            emailEdit.error = null
        }
        val password = passEdit.text.toString()
        if (TextUtils.isEmpty(password)) {
            passEdit.error = "Required"
            valid = false
        } else {
            passEdit.error = null
        }
        return valid
    }



    private fun signInUser(email: String, password: String) {
        if (validateForm()) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI
                        Log.d(TAG, "signInWithEmail: Success")
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If Sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail: Failure", task.exception)
                        Toast.makeText(this@CrearCuentaActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }
    }


    private fun isEmailValid(emailStr: String?): Boolean {
        val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr)
        return matcher.find()
    }


    private fun login() {
        val email = emailEdit.text.toString()
        val pass = passEdit.text.toString()
        if (!isEmailValid(email)) {
            Toast.makeText(this@CrearCuentaActivity, "Email is not a valid format", Toast.LENGTH_SHORT).show()
            return
        }
        signInUser(email, pass)
    }


    private fun signUp() {
        val email = emailEdit.text.toString()
        val pass = passEdit.text.toString()
        val name = binding.name.text.toString()
        val userType = binding.userType.selectedItem.toString()
        if (!isEmailValid(email)) {
            Toast.makeText(this@CrearCuentaActivity, "Email is not a valid format", Toast.LENGTH_SHORT).show()
            return
        }
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = mAuth.currentUser
                val userId = user!!.uid
                val database = FirebaseDatabase.getInstance().getReference("users")
                val userInfo = mapOf(
                    "email" to email,
                    "name" to name,
                    "userType" to userType
                )
                database.child(userId).setValue(userInfo).addOnCompleteListener {
                    Toast.makeText(
                        this@CrearCuentaActivity,
                        String.format("The user %s is successfully registered", user.email),
                        Toast.LENGTH_LONG
                    ).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(this@CrearCuentaActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }.addOnFailureListener(this) { e ->
            Toast.makeText(this@CrearCuentaActivity, e.message, Toast.LENGTH_LONG).show() }
    }


    private fun forgotPassword() {
        val email = emailEdit.text.toString()
        if (isEmailValid(email)) {
            Toast.makeText(
                this@CrearCuentaActivity,
                "Email is not a valid format",
                Toast.LENGTH_SHORT)
                .show()
            return
        }
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this) {
            Toast.makeText(
                this@CrearCuentaActivity,
                "Email instructions hace been sent, please check your email",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleLoginSuccess() {
        val email = emailEdit.text.toString()
        val password = passEdit.text.toString()

        // Autenticamos al usuario en Firebase después de la autenticación biométrica
        signInUser(email, password)
    }
}