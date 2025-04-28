package com.example.health_connect

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.health_connect.ChatApplication.GeminiHelper
import java.util.Calendar

class ChatActivity : AppCompatActivity() {

    private lateinit var chatrecyclerview: RecyclerView
    private lateinit var sendbutton: ImageView
    private lateinit var messagebox: EditText
    private lateinit var adapter: MessageAdapter
    var recieverroom: String? = null
    var senderroom: String? = null
    private lateinit var mDbref: DatabaseReference
    private lateinit var messageList: ArrayList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatrecyclerview = findViewById(R.id.chatrecyclerview)
        sendbutton = findViewById(R.id.sendbutton)
        messagebox = findViewById(R.id.messagebox)
        messageList = ArrayList()
        adapter = MessageAdapter(this, messageList)
        val intent = intent
        val name = intent.getStringExtra("name")
        val receiveruid = intent.getStringExtra("uid")
        val toolbar: Toolbar = findViewById(R.id.toolbar_chat)
        toolbar.title = name
        setSupportActionBar(toolbar)
        val senderuid = FirebaseAuth.getInstance().currentUser?.uid

        if (receiveruid != null && senderuid != null) {
            senderroom = receiveruid + senderuid
            recieverroom = senderuid + receiveruid
        } else {
            Log.e("ChatActivity", "Receiver UID or Sender UID is null")
            return
        }

        mDbref = FirebaseDatabase.getInstance().getReference()
        chatrecyclerview.layoutManager = LinearLayoutManager(this)
        chatrecyclerview.adapter = adapter

        // Logic for recycler view
        mDbref.child("chats").child(senderroom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postsnapshot in snapshot.children) {
                        val messsage = postsnapshot.getValue(Message::class.java)
                        if (messsage != null) {
                            messsage.message?.let {
                                GeminiHelper.isSpam(it) { isSpam ->
                                    Log.d("SpamCheck", "Message: $it, Is Spam: $isSpam")
                                    if (!isSpam) {
                                        runOnUiThread {
                                            messageList.add(messsage)
                                            adapter.notifyDataSetChanged()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatActivity", "Error fetching messages", error.toException())
                }
            })

        sendbutton.setOnClickListener {
            val message = messagebox.text.toString().trim()
            if (message.isNotEmpty() && message.isNotBlank()) {
                sendNotification("New Message", "You have a new message!")
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR).toString()
                val monthNames = arrayOf(
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                )
                val monthNumber = c.get(Calendar.MONTH)
                val month = monthNames[monthNumber]
                val day = c.get(Calendar.DAY_OF_MONTH).toString()
                val hour = c.get(Calendar.HOUR_OF_DAY).toString()
                val minute = c.get(Calendar.MINUTE).toString()
                val date = "$day $month $year"
                val time = "$hour:$minute"
                val senderuid = FirebaseAuth.getInstance().currentUser?.uid
                val messageobject = Message(message, senderuid, date, time)

                mDbref.child("chats").child(senderroom!!).child("messages").push()
                    .setValue(messageobject).addOnSuccessListener {
                        mDbref.child("chats").child(recieverroom!!).child("messages").push()
                            .setValue(messageobject)
                    }
                messagebox.setText("")
            }
        }
    }

    private fun sendNotification(title: String, body: String) {
        val notificationBuilder = NotificationCompat.Builder(this, "channel_id")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
            return
        }
        notificationManager.notify(0, notificationBuilder.build())
    }
}