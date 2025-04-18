package com.example.health_connect.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.health_connect.R
import com.example.health_connect.VideoConferencing.VideoCallActivity

class TeamFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_team, container, false)

        val roomIdInput = view.findViewById<EditText>(R.id.roomIdInput)
        val joinButton = view.findViewById<Button>(R.id.joinButton)

        joinButton.setOnClickListener {
            val roomId = roomIdInput.text.toString()
            if (roomId.isNotEmpty()) {
                // Pass the room ID to VideoCallActivity
                val intent = Intent(requireContext(), VideoCallActivity::class.java)
                intent.putExtra("ROOM_ID", roomId)
                startActivity(intent)
            }
        }

        return view
    }
}
