package com.example.sleepontrack_app

import android.content.Intent
import com.example.sleepontrack_app.firestore.User
import com.example.sleepontrack_app.firestore.FirestoreManagement
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)
        auth = FirebaseAuth.getInstance()

        val nicknameEditText = findViewById<EditText>(R.id.nickname_text)
        val emailEditText = findViewById<EditText>(R.id.email_text)
        val password1EditText = findViewById<EditText>(R.id.password1_text)
        val password2EditText = findViewById<EditText>(R.id.password2_text)
        val registerButton = findViewById<Button>(R.id.register_button)

        registerButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password1 = password1EditText.text.toString()
            val password2 = password2EditText.text.toString()

            if (nickname.isEmpty() || email.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
                Toast.makeText(this, "Fill all boxes", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(nickname.length <= 5){
                Toast.makeText(this, "Your nickname is too short", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!email.contains("@") || !email.contains(".")){
                Toast.makeText(this, "Your email is wrong. Please check if it contains of '@' and '.' ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password1 != password2) {
                Toast.makeText(this, "Passwords have to be the same", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(password1.length <6) {
                Toast.makeText(this, "Password is too short", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else {
                val hasUppercase = password1.any { it.isUpperCase() }
                val hasDigit = password1.any { it.isDigit() }
                val hasSpecialChar = password1.any { !it.isLetterOrDigit() }

                if (!hasUppercase || !hasDigit || !hasSpecialChar) {
                    Toast.makeText(
                        this,
                        "Password must contain at least one uppercase letter, one digit, and one special character.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
            }

            auth.createUserWithEmailAndPassword(email, password1)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                        val user = User(id = userId, nickname = nickname)

                        val db = FirebaseFirestore.getInstance()
                        db.collection("users").document(userId).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "User registered and saved", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, ClockActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error saving user: ${e.message}", Toast.LENGTH_LONG).show()
                            }

                    } else {
                        Toast.makeText(this, "Błąd: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            Toast.makeText(this, "Registration completed", Toast.LENGTH_SHORT).show()
        }
    }
}
