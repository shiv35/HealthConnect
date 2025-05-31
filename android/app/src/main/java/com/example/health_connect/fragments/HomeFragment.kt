package com.example.health_connect.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.health_connect.BlogRelated.CancerBlog
import com.example.health_connect.BlogRelated.CovidBlog
import com.example.health_connect.BlogRelated.FeverBlog
import com.example.health_connect.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var userImageLoad: ImageView
    private lateinit var UsernameDashboard: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        userImageLoad = view.findViewById(R.id.userImageLoad)
        UsernameDashboard = view.findViewById(R.id.Username_dashboard) // Initialize UsernameDashboard here

        val currentUser = FirebaseAuth.getInstance().currentUser
        val cardView_Covid = view.findViewById<CardView>(R.id.CovidBlog)
        val cardView_Fever = view.findViewById<CardView>(R.id.feverBlog)
        val cardView_Cancer = view.findViewById<CardView>(R.id.CancerBlog)

        cardView_Covid.setOnClickListener {
            val intent = Intent(requireContext(), CovidBlog::class.java)
            startActivity(intent)
        }
        cardView_Fever.setOnClickListener {
            val intent = Intent(requireContext(),FeverBlog::class.java)
            startActivity(intent)
        }
        cardView_Cancer.setOnClickListener {
            val intent = Intent(requireContext(),CancerBlog::class.java)
            startActivity(intent)
        }

        if (currentUser != null) {
            val userId = currentUser.uid
            Log.d("UserID", "Current user ID: $userId")
            // Load user data from Firebase Realtime Database
            loadUserData(userId)
        } else {
            UsernameDashboard.text = "Welcome User"
            Log.d("UserID", "No user is currently logged in.")
        }

        return view
    }

    private fun loadUserData(userId: String) {
        val userReference = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java)
                val profileImageUrl = snapshot.child("profileurl").getValue(String::class.java)

                // Update username TextView
                if (name != null) {
                    UsernameDashboard.text = "Welcome $name"
                } else {
                    UsernameDashboard.text = "Welcome User"
                }

                // Update profile image
                if (profileImageUrl != null) {
                    Glide.with(this@HomeFragment)
                        .load(profileImageUrl)
                        .into(userImageLoad)
                } else {
                    Toast.makeText(activity, "No profile image found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Error loading data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
