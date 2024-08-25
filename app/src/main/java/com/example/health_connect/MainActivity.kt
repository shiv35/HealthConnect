package com.example.health_connect

import android.content.ClipData.Item
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.health_connect.databinding.ActivityMainBinding
import com.example.health_connect.fragments.ChatFragment
import com.example.health_connect.fragments.HomeFragment
import com.example.health_connect.fragments.PrescriptionFragment
import com.example.health_connect.fragments.TeamFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fragmentManager : FragmentManager
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
// for navigation drawer
        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,R.string.app_name,R.string.email)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        // for bottom navigation
        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.bottom_home -> openFragment(HomeFragment())
                R.id.bottom_chat -> {
                    val intent = Intent(this, ChatMainActivity::class.java)
                    startActivity(intent)
                }

                R.id.bottom_prescription -> openFragment(PrescriptionFragment())
            }
             true
        }
        // first default fragment
        fragmentManager = supportFragmentManager
        openFragment(HomeFragment())

        binding.fab.setOnClickListener{
            Toast.makeText(this, "action button", Toast.LENGTH_SHORT).show()
        }


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_chat -> openFragment(ChatFragment())
            R.id.nav_home -> openFragment(HomeFragment())
            R.id.nav_team -> openFragment(TeamFragment())
            R.id.nav_prescription -> Toast.makeText(this, "prescription", Toast.LENGTH_SHORT).show()

        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed(){
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
         super.onBackPressed()
        }
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()

    }


}

