package com.example.health_connect.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
//import com.example.health_connect.ChatSection.ChatActivity
import com.example.health_connect.MainActivity
import com.example.health_connect.R
import com.example.health_connect.databinding.FragmentChatBinding


class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        val view = binding.root

        binding.butInChat.setOnClickListener {
            val intent = Intent(requireContext(), com.example.health_connect.ChatActivity::class.java)
            startActivity(intent)

            Toast.makeText(context, "Opening chat...", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }
}
