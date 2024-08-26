package com.example.health_connect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Signup : AppCompatActivity() {
    private lateinit var name: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var signupButton: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var loginAct: TextView
    private lateinit var mDbRef: DatabaseReference
    private lateinit var uploadImage: ImageView
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        // Initialize UI elements
        name = findViewById(R.id.name)
        username = findViewById(R.id.email)
        password = findViewById(R.id.setpassword)
        confirmPassword = findViewById(R.id.ConfirmPassword)
        signupButton = findViewById(R.id.signup_button)
        loginAct = findViewById(R.id.RedirectLogin)
        uploadImage = findViewById(R.id.registerUserImage)

        loginAct.setOnClickListener {
            val intent = Intent(this@Signup, Login::class.java)
            startActivity(intent)
            finish()
        }

        uploadImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select image"),
                PICK_IMAGE_REQUEST
            )
        }

        signupButton.setOnClickListener {
            val nameText = name.text.toString().trim()
            val email = username.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val confirmPasswordText = confirmPassword.text.toString().trim()
            if (nameText.isNotEmpty() && email.isNotEmpty() && passwordText.isNotEmpty() && confirmPasswordText == passwordText) {
                signup(nameText, email, passwordText)
            } else if (confirmPasswordText != passwordText) {
                Toast.makeText(this, "Please fill the same password.", Toast.LENGTH_SHORT).show()
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
                        uploadProfileImageAndAddUser(name, email, uid)
                    } else {
                        Toast.makeText(this, "User ID is null after signup.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        baseContext,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun uploadProfileImageAndAddUser(name: String, email: String, uid: String) {
        if (imageUri != null) {
            Log.d("test","$imageUri")
            val storageReference: StorageReference = storage.reference.child("profile_image/$uid.jpg")
            storageReference.putFile(imageUri!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storageReference.downloadUrl.addOnCompleteListener { imageTask ->
                            if (imageTask.isSuccessful) {
                                val imageUrl = imageTask.result.toString()
                                Log.d("testurl","$imageUri")
                                addUserToDatabase(name, email, uid, imageUrl)
                            } else {
                                addUserToDatabase(name, email, uid,null)
                                Toast.makeText(this, "Failed to get image URL.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        addUserToDatabase(name, email, uid, "")
                        Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            addUserToDatabase(name, email, uid, null)
        }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String, profileUrl: String?) {
        val user = User(Name = name, email = email, uid = uid, profileurl = profileUrl)
        mDbRef = database.getReference("users")
        mDbRef.child(uid).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User data saved successfully.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Signup, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to save user data: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Glide.with(this)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(uploadImage)
        }
    }
}
