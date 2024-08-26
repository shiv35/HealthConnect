package com.example.health_connect.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.health_connect.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var userImageLoad: ImageView
//    private lateinit var userReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        userImageLoad = view.findViewById(R.id.userImageLoad)

        // Assuming you have the userId available
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // Get the user's UID
            val userId = currentUser.uid

            // Now you can use this UID to perform actions, e.g., access the user's data in Firebase
            Log.d("UserID", "Current user ID: $userId")
            loadUserProfileImage(userId)
        } else {
            // No user is logged in
            Log.d("UserID", "No user is currently logged in.")
        }


        return view
    }

    private fun loadUserProfileImage(userId: String) {
        val userReference = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        userReference.child("profileurl").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)
                if (profileImageUrl != null) {
                    Glide.with(this@HomeFragment)
                        .load(profileImageUrl)
                        .into(userImageLoad)  // Ensure this matches your ImageView's ID
                } else {
                    Toast.makeText(activity, "No profile image found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Error loading image: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
