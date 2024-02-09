package com.example.health_connect.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.example.health_connect.R

class ForgotPassword : AppCompatActivity() {
    private lateinit var ivBack: ImageView
    private lateinit var countrySpinner: Spinner
    private lateinit var selectedCountryTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        ivBack = findViewById(R.id.back1)
        countrySpinner = findViewById(R.id.countrySpinner)
        selectedCountryTextView = findViewById(R.id.selectedCountryTextView)

        val countriesArray = resources.getStringArray(R.array.countries_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countriesArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        countrySpinner.adapter = adapter

        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCountryCode = countriesArray[position]
                selectedCountryTextView.text = "Selected Country Code: $selectedCountryCode"
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        ivBack.setOnClickListener {
            val intent = Intent(this@ForgotPassword, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}