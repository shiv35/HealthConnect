package com.example.health_connect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.health_connect.Login
import com.example.health_connect.User
import com.example.health_connect.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatMainActivity : AppCompatActivity() {

    private lateinit var userRecycler: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mdRef: DatabaseReference
    private lateinit var logoutButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatmain)

        // Initialize Firebase Authentication and Database references
        mAuth = FirebaseAuth.getInstance()
        mdRef = FirebaseDatabase.getInstance().getReference("users") // Reference to "users" node

        // Initialize UI elements
        userList = ArrayList()
        adapter = UserAdapter(userList, this)

        userRecycler = findViewById(R.id.userRecyclerview)
        userRecycler.layoutManager = LinearLayoutManager(this)
        userRecycler.adapter = adapter

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Health Connect"
        setSupportActionBar(toolbar)

        logoutButton = findViewById(R.id.logout_Button)
        logoutButton.setOnClickListener {
            mAuth.signOut()
            val intentNew = Intent(this@ChatMainActivity, Login::class.java)
            startActivity(intentNew)
            finish()
        }

        // Fetch users from the database
        fetchUsers()
    }

    private fun fetchUsers() {
        mdRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()  // Clear the list before adding new data
                val currentUserUid = mAuth.currentUser?.uid

                for (postSnapshot in snapshot.children) {
                    val user = postSnapshot.getValue(User::class.java)
                    // Add user to the list if it's not the current logged-in user
                    if (user != null && user.uid != currentUserUid) {
                        userList.add(user)
                    }
                }
                adapter.notifyDataSetChanged()  // Notify the adapter to refresh the RecyclerView
            }

            override fun onCancelled(error: DatabaseError) {
                // Log error if data retrieval fails
                Log.e("MainActivity", "Error fetching users", error.toException())
            }
        })
    }
}
