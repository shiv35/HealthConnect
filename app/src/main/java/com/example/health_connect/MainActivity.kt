package com.example.health_connect

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.health_connect.databinding.ActivityMainBinding
import com.example.health_connect.fragments.ChatFragment
import com.example.health_connect.fragments.HomeFragment
import com.example.health_connect.fragments.PrescriptionFragment
import com.example.health_connect.fragments.TeamFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        mAuth = FirebaseAuth.getInstance()

        // Set up toolbar and drawer
        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.app_name, R.string.email)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        // Load the user's profile image in the nav header
        val headerView = binding.navigationDrawer.getHeaderView(0)
        val userImageView = headerView.findViewById<ImageView>(R.id.register_userimage)
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            loadUserProfileImage(currentUser.uid, userImageView)
        }

        // Set up bottom navigation
        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> openFragment(HomeFragment())
                R.id.bottom_chat -> {
                    val intent = Intent(this, ChatMainActivity::class.java)
                    startActivity(intent)
                }
                R.id.bottom_prescription -> openFragment(PrescriptionFragment())
                R.id.bottom_team -> openFragment(TeamFragment())
            }
            true
        }

        // Load default fragment
        fragmentManager = supportFragmentManager
        openFragment(HomeFragment())

        binding.fab.setOnClickListener {
            Toast.makeText(this, "Action button", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_chat -> openFragment(ChatFragment())
            R.id.nav_home -> openFragment(HomeFragment())
            R.id.nav_team -> openFragment(TeamFragment())
            R.id.nav_prescription -> openFragment(PrescriptionFragment())
            R.id.nav_logout -> {
                mAuth.signOut()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    private fun loadUserProfileImage(userId: String, userImageView: ImageView) {
        val userReference = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        userReference.child("profileurl").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)
                if (profileImageUrl != null) {
                    Glide.with(this@MainActivity)
                        .load(profileImageUrl)
                        .into(userImageView)
                } else {
                    Toast.makeText(this@MainActivity, "Profile image not found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error loading image: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
