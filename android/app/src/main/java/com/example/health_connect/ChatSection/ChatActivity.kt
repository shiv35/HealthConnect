//package com.example.health_connect.ChatSection
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import com.example.health_connect.ChatApplication.GeminiHelper
//import com.example.health_connect.R
//import im.zego.zegoexpress.callback.IZegoIMSendBroadcastMessageCallback
//import im.zego.zegoexpress.callback.IZegoEventHandler
//import im.zego.zegoexpress.entity.ZegoBroadcastMessageInfo
//import im.zego.zegoexpress.entity.ZegoUser
//import java.util.ArrayList
//
//class ChatActivity : AppCompatActivity() {
//    private val userID = "user_${System.currentTimeMillis()}"
//    private val roomID = "demo_room"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_chat2)
//
//        // Log in to room first
//        MyApp.engine.loginRoom(roomID, ZegoUser(userID, "User"))
//
//        val sendBtn = findViewById<Button>(R.id.btnSend)
//        val messageEdit = findViewById<EditText>(R.id.editMessage)
//
//        sendBtn.setOnClickListener {
//            val msg = messageEdit.text.toString().trim()
//            if (msg.isEmpty()) return@setOnClickListener
//
//            // Check spam via Gemini
//            GeminiHelper.isSpam(msg) { isSpam ->
//                runOnUiThread {
//                    if (isSpam) {
//                        Toast.makeText(this, "Spam detected. Message blocked.", Toast.LENGTH_SHORT).show()
//                    } else {
//                        // Correctly pass roomID, message, and callback
//                        MyApp.engine.sendBroadcastMessage(
//                            roomID,
//                            msg,
//                            object : IZegoIMSendBroadcastMessageCallback {
//                                override fun onIMSendBroadcastMessageResult(errorCode: Int, messageID: Long) {
//                                    // Callback may come off the UI thread, so ensure UI updates run on it
//                                    runOnUiThread {
//                                        if (errorCode == 0) {
//                                            Toast.makeText(
//                                                this@ChatActivity,
//                                                "Message sent successfully.",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                            messageEdit.text.clear()
//                                        } else {
//                                            Toast.makeText(
//                                                this@ChatActivity,
//                                                "Failed to send message: error $errorCode",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        }
//                                    }
//                                }
//                            }
//                        )
//                    }
//                }
//            }
//        }
//
//        // Receive messages
//        MyApp.engine.setEventHandler(object : IZegoEventHandler() {
//            override fun onIMRecvBroadcastMessage(
//                roomID: String?,
//                messageList: ArrayList<ZegoBroadcastMessageInfo>?
//            ) {
//                messageList?.forEach { message ->
//                    runOnUiThread {
//                        Toast.makeText(this@ChatActivity, "Received: ${message.message}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        })
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        MyApp.engine.logoutRoom(roomID)
//    }
//}
