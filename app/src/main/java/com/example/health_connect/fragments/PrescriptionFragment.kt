package com.example.health_connect.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.health_connect.InputDataClass
import com.example.health_connect.ResponseDataClass
import com.example.health_connect.RetrofitInstance
import com.example.health_connect.databinding.FragmentPrescriptionBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PrescriptionFragment : Fragment() {
    private lateinit var binding : FragmentPrescriptionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPrescriptionBinding.inflate(inflater, container, false)
        val view=binding.root
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        binding.predictBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Button Pressed", Toast.LENGTH_SHORT).show()
            getData()
        }
        return view
    }
    private fun getData() {

        val symptoms =  binding.editTextSymptoms.text.toString().trim()

        if (symptoms.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter symptoms", Toast.LENGTH_SHORT).show()
            return
        }

        val request = InputDataClass(symptom = symptoms)

        val progressDialog = ProgressDialog(requireContext()).apply {
            setMessage("Please wait while data is fetching")
            show()
        }

        RetrofitInstance.apiService.getPrediction(request).enqueue(object :
            Callback<ResponseDataClass?> {
            override fun onResponse(call: Call<ResponseDataClass?>, response: Response<ResponseDataClass?>) {
                progressDialog.dismiss()

                if (response.isSuccessful && response.body() != null) {
                    val predicted = response.body()?.predicted_disease
                    val description = response.body()?.description
                    val precaution = response.body()?.precautions
                    val medication = response.body()?.medications
                    val diet = response.body()?.diet
                    val workout = response.body()?.workout


                    binding.predictedDisease.text=predicted
                    binding.Description.text=description
                    binding.Medication.text = medication.toString()
                    binding.Workout.text=workout.toString()
                    binding.Diet.text=diet.toString()
                    binding.Precaution.text=precaution.toString()


//
                } else {
                    Log.e("API_ERROR", "Response failed or body is null: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseDataClass?>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("API_FAILURE", "Error fetching data: ${t.message}", t)
                Toast.makeText(requireContext(), "Failed to fetch data: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

}