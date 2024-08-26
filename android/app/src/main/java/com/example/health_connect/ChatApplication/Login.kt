package com.example.health_connect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {

    private lateinit var Username: EditText
    private lateinit var Password: EditText
    private lateinit var Signup_Button: Button
    private lateinit var Login: Button
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        FirebaseApp.initializeApp(this)

        // Check if a user is already authenticated
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            mAuth = FirebaseAuth.getInstance()
            Username = findViewById(R.id.username)
            Password = findViewById(R.id.password)
            Signup_Button = findViewById(R.id.signup_login)
            Login = findViewById(R.id.loginbutton)

            Signup_Button.setOnClickListener {
                val intent = Intent(this@Login, Signup::class.java)
                startActivity(intent)
            }

            Login.setOnClickListener {
                val email = Username.text.toString().trim()
                val password = Password.text.toString().trim()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    login(email, password)
                } else {
                    Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Fetch and save user data to Firebase Realtime Database after successful login
                    saveUserDataToDatabase()

                    // Redirect to MainActivity
                    val intent = Intent(this@Login, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign-in fails, display a message to the user
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun saveUserDataToDatabase() {
        // Get the current logged-in user's UID
        val userId = mAuth.currentUser?.uid
        val userName = Username.text.toString()
        val email = mAuth.currentUser?.email

        if (userId != null) {
            // Fetch profile URL from database and save user data
            getProfileUrl(userId) { profileUrl ->
                // Create a User object with the logged-in user's data
                val user = User(email ,userName, userId,  profileUrl)

                // Save the user data to Firebase Realtime Database under the "users" node
                FirebaseDatabase.getInstance().getReference("users").child(userId)
                    .setValue(user)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // User data saved successfully
                            Toast.makeText(this, "User data saved successfully.", Toast.LENGTH_SHORT).show()
                        } else {
                            // Handle the error
                            Toast.makeText(this, "Failed to save user data.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun getProfileUrl(userId: String, callback: (String?) -> Unit) {
        val userReference = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        userReference.child("profileurl").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)
                callback(profileImageUrl)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Login, "Error retrieving profile URL: ${error.message}", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }
}
