package com.example.health_connect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Signup : AppCompatActivity() {
    private lateinit var name: EditText
    private lateinit var Username: EditText
    private lateinit var Password: EditText
    private lateinit var Signup_Button: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance()

        // Initialize UI elements
        name = findViewById(R.id.name)
        Username = findViewById(R.id.email)
        Password = findViewById(R.id.setpassword)
        Signup_Button = findViewById(R.id.signup_button)

        Signup_Button.setOnClickListener {
            val nameText = name.text.toString().trim()
            val email = Username.text.toString().trim()
            val password = Password.text.toString().trim()

            if (nameText.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                signup(nameText, email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signup(name: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser?.uid
                    if (uid != null) {
                        addUserToDatabase(name, email, uid)
                        val intent = Intent(this@Signup, ChatMainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("Signup", "User ID is null after signup.")
                    }
                } else {
                    Log.e("Signup", "Authentication failed.", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
        val user = User(Name = name, email = email, uid = uid)
        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            mDbRef = FirebaseDatabase.getInstance().getReference("users")
            mDbRef.child(uid).setValue(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Signup", "User data saved successfully.")
                    } else {
                        Log.e("Signup", "Failed to save user data.", task.exception)
                        Toast.makeText(this, "Failed to save user data: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Log.e("Signup", "User is not authenticated.")
            Toast.makeText(this, "User is not authenticated. Cannot save data.", Toast.LENGTH_LONG).show()
        }
    }
}
