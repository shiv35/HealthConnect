package com.example.health_connect.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.health_connect.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegistrationActivity : AppCompatActivity() {
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

    private lateinit var auth: FirebaseAuth
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

        auth = FirebaseAuth.getInstance()

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
                        showToast("Successfully Signed Up")
                        finish()
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
    }
