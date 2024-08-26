package com.example.health_connect

data class ResponseDataClass(
    val description: String,
    val diet: List<String>,
    val medications: List<String>,
    val precautions: List<List<String>>,
    val predicted_disease: String,
    val workout: List<String>
)