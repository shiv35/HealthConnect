package com.example.health_connect.VideoConferencing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.health_connect.R

import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceConfig
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceFragment
import java.util.Random

class VideoCallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        val roomId = intent.getStringExtra("ROOM_ID")
        if (roomId != null) {
            addConf(roomId)
        }
    }

    private fun addConf(roomId: String) {
        val appId: Long = 153350051
        val appSign: String = "6e97bbce7e5208c94cbfb7bb08149376c09d88633a00afbe53e95c5083f88425"
        val confId: String = roomId // Use the roomId from the intent
        val userId: String = generate()
        val userName: String = "${userId}_Name"
        val config = ZegoUIKitPrebuiltVideoConferenceConfig()

        // Create a new fragment instance and pass the parameters
        val fragment = ZegoUIKitPrebuiltVideoConferenceFragment.newInstance(
            appId,
            appSign,
            userId,
            userName,
            confId, config
        )

        // Add the fragment to the activity
        supportFragmentManager.beginTransaction().replace(R.id.conference, fragment).commitNow()
    }

    private fun generate(): String {
        val builder = StringBuilder()
        val random = Random()
        while (builder.length < 5) {
            val nextInt = random.nextInt(10)
            if (builder.isEmpty() && nextInt == 0) {
                continue
            }
            builder.append(nextInt)
        }
        return builder.toString()
    }
}