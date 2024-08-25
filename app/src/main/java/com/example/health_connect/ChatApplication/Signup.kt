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

class Signup : AppCompatActivity() {
    private lateinit var name: EditText
    private lateinit var Username: EditText
    private lateinit var Password: EditText
    private lateinit var ConfirmPassword: EditText
    private lateinit var Signup_Button: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var loginAct : TextView
    private lateinit var mDbRef: DatabaseReference
    private lateinit var UploadImage : ImageView
    private lateinit var database:FirebaseDatabase
    private lateinit var storage : FirebaseStorage

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance()

        database=FirebaseDatabase.getInstance()
        storage=FirebaseStorage.getInstance()

        // Initialize UI elements
        name = findViewById(R.id.name)
        Username = findViewById(R.id.email)
        Password = findViewById(R.id.setpassword)
        ConfirmPassword = findViewById(R.id.ConfirmPassword)
        Signup_Button = findViewById(R.id.signup_button)
        loginAct = findViewById(R.id.RedirectLogin)
        UploadImage = findViewById(R.id.registerUserImage)


        loginAct.setOnClickListener {
            val intent = Intent(this@Signup, Login::class.java)
            startActivity(intent)
            finish()
        }

        UploadImage.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent,"select image"),
                PICK_IMAGE_REQUEST
            )
        }
        Signup_Button.setOnClickListener {
            val nameText = name.text.toString().trim()
            val email = Username.text.toString().trim()
            val password = Password.text.toString().trim()
            val confirmPassword = ConfirmPassword.text.toString().trim()
            if (nameText.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword == password) {
                signup(nameText, email, password)
            }
            else if(confirmPassword!= password)
            {
                Toast.makeText(this, "Please fill the same password.", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signup(name: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser?.uid
                    val userReference = database.getReference("users")
                    if (uid != null) {
                        addUserToDatabase(name, email, uid)
                        val storageReference =
                            storage.reference.child("profile_image/$uid.jpg")

                        storageReference.putFile(imageUri!!).addOnCompleteListener { task->
                            if(task.isSuccessful){
                                storageReference.downloadUrl.addOnCompleteListener { imageUri ->
                                    if (imageUri.isSuccessful) {
                                        val imageUrl = imageUri.result.toString()
                                        // save image url to realtime database
                                        userReference.child(uid)
                                            .child("profileImage").setValue(imageUrl)
                                    }
                                }
                            }
                        }
                        val intent = Intent(this@Signup, MainActivity::class.java)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.data !=null)

            imageUri = data.data
        Glide.with(this)
            .load(imageUri)
            .apply(RequestOptions.circleCropTransform())
            .into(UploadImage)
    }
}
