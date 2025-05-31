package com.example.health_connect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
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

    // UI references
    private lateinit var name: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var signupButton: Button
    private lateinit var loginAct: TextView
    private lateinit var uploadImage: ImageView

    // Registration Number + validation icons
    private lateinit var doctorCertNumber: EditText
    private lateinit var progressCert: ProgressBar
    private lateinit var doneCert: ImageView

    // Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    // Delay for showing “loading” spinner (2 seconds)
    private val LOADING_DELAY_MS = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // ====== 1) Initialize Firebase ======
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        // ====== 2) Bind all UI elements ======
        name = findViewById(R.id.name)
        username = findViewById(R.id.email)
        password = findViewById(R.id.setpassword)
        confirmPassword = findViewById(R.id.ConfirmPassword)
        signupButton = findViewById(R.id.signup_button)
        loginAct = findViewById(R.id.RedirectLogin)
        uploadImage = findViewById(R.id.registerUserImage)

        doctorCertNumber = findViewById(R.id.doctorCertNumber)
        progressCert = findViewById(R.id.progressCert)
        doneCert = findViewById(R.id.doneCert)

        val isDoctorCheckbox = findViewById<CheckBox>(R.id.isDoctorCheckbox)

        // Eye icons for toggling password visibility
        val eyeIcon = findViewById<ImageView>(R.id.eye)
        val eyeIcon1 = findViewById<ImageView>(R.id.eye1)

        // ====== 3) “Login” text listener → go to Login screen ======
        loginAct.setOnClickListener {
            val intent = Intent(this@Signup, Login::class.java)
            startActivity(intent)
            finish()
        }

        // ====== 4) “Are you a doctor?” checkbox logic ======
        isDoctorCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                doctorCertNumber.visibility = View.VISIBLE
            } else {
                doctorCertNumber.visibility = View.GONE
                progressCert.visibility = View.GONE
                doneCert.visibility = View.GONE
                doctorCertNumber.text.clear()
            }
        }

        // ====== 5) Profile image picker ======
        uploadImage.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(
                Intent.createChooser(intent, "Select image"),
                PICK_IMAGE_REQUEST
            )
        }

        // ====== 6) Sign Up button logic (basic validation) ======
        signupButton.setOnClickListener {
            val nameText = name.text.toString().trim()
            val emailText = username.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val confirmPasswordText = confirmPassword.text.toString().trim()

            when {
                nameText.isEmpty() || emailText.isEmpty() ||
                        passwordText.isEmpty() || confirmPasswordText.isEmpty() -> {
                    Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                }
                confirmPasswordText != passwordText -> {
                    Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // If everything is valid, create the user
                    signup(nameText, emailText, passwordText)
                }
            }
        }

        // ====== 7) Password toggle logic ======
        // Start both password fields in “hidden” mode
        password.transformationMethod = PasswordTransformationMethod.getInstance()
        confirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        eyeIcon.setOnClickListener {
            togglePasswordVisibility(password, eyeIcon)
        }
        eyeIcon1.setOnClickListener {
            togglePasswordVisibility(confirmPassword, eyeIcon1)
        }

        // ====== 8) Registration Number “5‐chars → loading → done” ======
        doctorCertNumber.addTextChangedListener(
            createFiveCharWatcher(doctorCertNumber, progressCert, doneCert)
        )
    }

    /**
     * Returns a TextWatcher that:
     * 1) Shows the small spinner (progressBar) as soon as the EditText length == 5
     * 2) After LOADING_DELAY_MS, hides the spinner and shows the checkmark (doneImage)
     * 3) If length != 5, hides both spinner & checkmark
     */
    private fun createFiveCharWatcher(
        editText: EditText,
        progressBar: ProgressBar,
        doneImage: ImageView
    ): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { /* no-op */ }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { /* no-op */ }

            override fun afterTextChanged(s: Editable?) {
                val length = s?.length ?: 0

                if (length == 5) {
                    // 1) Show spinner, hide checkmark
                    progressBar.visibility = View.VISIBLE
                    doneImage.visibility = View.GONE

                    // 2) After 2 seconds, hide spinner & show checkmark
                    Handler(Looper.getMainLooper()).postDelayed({
                        progressBar.visibility = View.GONE
                        doneImage.visibility = View.VISIBLE
                    }, LOADING_DELAY_MS)

                } else {
                    // If user deletes back to <5, hide both
                    progressBar.visibility = View.GONE
                    doneImage.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Toggle between showing and hiding text in a password field.
     * Optionally, you can swap the eyeIcon resource here if you have an "eye_off" asset.
     */
    private fun togglePasswordVisibility(editText: EditText, eyeIcon: ImageView) {
        if (editText.transformationMethod is PasswordTransformationMethod) {
            // Currently hidden → show
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            // If you have an “eye‐off” drawable, uncomment below and replace with your asset:
            // eyeIcon.setImageResource(R.drawable.ic_eye_off)
        } else {
            // Currently visible → hide
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            // Swap back to the “eye” icon if you changed it:
            // eyeIcon.setImageResource(R.drawable.eye)
        }
        editText.setSelection(editText.text.length)
    }

    /**
     * Sign up via Firebase Auth and then upload the profile image & user info to Realtime Database.
     */
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
                        this,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    /**
     * Uploads the chosen image to Firebase Storage and then writes user data
     * (including image URL) to Realtime Database.
     */
    private fun uploadProfileImageAndAddUser(name: String, email: String, uid: String) {
        if (imageUri != null) {
            val storageReference: StorageReference = storage.reference.child("profile_image/$uid.jpg")
            storageReference.putFile(imageUri!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storageReference.downloadUrl.addOnCompleteListener { imageTask ->
                            if (imageTask.isSuccessful) {
                                val imageUrl = imageTask.result.toString()
                                addUserToDatabase(name, email, uid, imageUrl)
                            } else {
                                addUserToDatabase(name, email, uid, null)
                                Toast.makeText(this, "Failed to get image URL.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        addUserToDatabase(name, email, uid, "")
                        Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            // No image chosen → just write user data without a profile URL
            addUserToDatabase(name, email, uid, null)
        }
    }

    /**
     * Writes user data under /users/$uid in Realtime Database.
     */
    private fun addUserToDatabase(name: String, email: String, uid: String, profileUrl: String?) {
        val user = User(Name = name, email = email, uid = uid, profileurl = profileUrl)
        mDbRef = database.getReference("users")
        mDbRef.child(uid).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User data saved successfully.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@Signup, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Failed to save user data: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    /**
     * Receive result from image‐picker Intent.
     */
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
