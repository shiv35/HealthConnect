package com.example.health_connect.registration

import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.health_connect.MainActivity
import com.example.health_connect.R
import com.example.health_connect.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegistrationActivity : AppCompatActivity() {

    private lateinit var userImage: ImageView
    private lateinit var profileImage : CardView
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etEmailAddress: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var ivBack: ImageView
    private lateinit var eye: ImageView
    private lateinit var eye1: ImageView
    private lateinit var tvRedirectLogin: TextView

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri : Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        tvRedirectLogin= findViewById(R.id.RedirectLogin)
        etFirstName = findViewById(R.id.firstName)
        etLastName = findViewById(R.id.lastName)
        etEmailAddress = findViewById(R.id.email)
        etNewPassword = findViewById(R.id.password)
        etConfirmPassword = findViewById(R.id.ConfirmPassword)
        btnSignUp = findViewById(R.id.signUp)
        ivBack = findViewById(R.id.imageView14)
        eye = findViewById(R.id.eye)
        eye1 = findViewById(R.id.eye1)
        profileImage = findViewById(R.id.cardView2)
        userImage = findViewById(R.id.registerUserImage)

        auth = FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()
        storage=FirebaseStorage.getInstance()

        eye.setOnClickListener {
            togglePasswordVisibility(etNewPassword)
        }
        eye1.setOnClickListener {
            togglePasswordVisibility(etConfirmPassword)
        }
        btnSignUp.setOnClickListener {
//            CoroutineScope(Dispatchers.Main).launch {
                signUpUser()
//            }
        }

        profileImage.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent,"select image"),
                PICK_IMAGE_REQUEST
            )
        }

        tvRedirectLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        ivBack.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signUpUser() {
        val firstName = etFirstName.text.toString()
        val lastName = etLastName.text.toString()
        val email = etEmailAddress.text.toString()
        val password = etNewPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            showToast("All fields are required")
            return
        }

        if (password != confirmPassword) {
            showToast("Password and Confirm Password do not match")
            return
        }
        try {
            Toast.makeText(this, "pressed", Toast.LENGTH_SHORT).show()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        auth.signOut()
                        user?.let {
                            //storing data in realtime database
                            val userReference = database.getReference("users")
                            val userId = user.uid
                            val userData = UserData(firstName, email)
                            userReference.child(userId).setValue(userData)

                            //upload image to firebase storage
                            val storageReference =
                                storage.reference.child("profile_image/$userId.jpg")

                            storageReference.putFile(imageUri!!).addOnCompleteListener { task->
                                if(task.isSuccessful){
                                    storageReference.downloadUrl.addOnCompleteListener { imageUri ->
                                        if (imageUri.isSuccessful) {
                                            val imageUrl = imageUri.result.toString()
                                            // save image url to realtime database
                                            userReference.child(userId)
                                                .child("profileImage").setValue(imageUrl)
                                        }
                                    }
                                }
                            }

                            startActivity(Intent(this,MainActivity::class.java))
                            finish()
//                                    Toast.makeText(this, "successfully", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        showToast("Sign Up Failed")
                    }
                }

        } catch (e: Exception) {
            showToast("Sign Up Failed: ${e.message}")
        }
    }

    private fun showToast(message: String) {
//        withContext(Dispatchers.Main) {
            Toast.makeText(this@RegistrationActivity, message, Toast.LENGTH_SHORT).show()
//        }
    }

    private fun togglePasswordVisibility(editText: EditText) {
        val showPassword = editText.inputType != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        editText.inputType = if (showPassword) {
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        editText.setSelection(editText.text.length)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.data !=null)

            imageUri = data.data
        Glide.with(this)
            .load(imageUri)
            .apply(RequestOptions.circleCropTransform())
            .into(userImage)
    }
    }
