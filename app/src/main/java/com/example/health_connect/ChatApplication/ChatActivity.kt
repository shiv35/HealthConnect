package com.example.health_connect

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.health_connect.MessageAdapter
import com.example.health_connect.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.messaging.FirebaseMessaging
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
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
        mDbref = FirebaseDatabase.getInstance().getReference()
        senderroom = receiveruid + senderuid
        recieverroom = senderuid + receiveruid
        chatrecyclerview.layoutManager = LinearLayoutManager(this)
        chatrecyclerview.adapter = adapter

        // logic for recycler view
        mDbref.child("chats").child(senderroom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postsnapshot in snapshot.children) {
                        val message = postsnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        sendbutton.setOnClickListener {

            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    // Save the FCM token to your server (you need to implement this)
                    Log.d("FCM Token", token!!)
                } else {
                    Log.w("FCM Token", "Fetching FCM registration token failed", task.exception)
                }
            })

            val message = messagebox.text.toString().trim()
            if (message.isNotEmpty() && message.isNotBlank()) {
                chatrecyclerview.layoutManager?.smoothScrollToPosition(
                    chatrecyclerview,
                    null,
                    adapter.itemCount
                )
                sendNotification("New Message", "You have a new message!")
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR).toString()
                val monthNames = arrayOf(
                    "Jan",
                    "Feb",
                    "Mar",
                    "Apr",
                    "May",
                    "Jun",
                    "Jul",
                    "Aug",
                    "Sep",
                    "Oct",
                    "Nov",
                    "Dec"
                )
                val monthNumber = c.get(Calendar.MONTH)
                val month = monthNames[monthNumber]
                val day = c.get(Calendar.DAY_OF_MONTH).toString()
                val hour = c.get(Calendar.HOUR_OF_DAY).toString()
                val minute = c.get(Calendar.MINUTE).toString()
//            val formatter1 = DateTimeFormatter.ofPattern("dd MMM yyyy")
                val date = day + " " + month + " " + year
                val time = hour + ":" + minute
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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channel_id",
                "Channel Name",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(0, notificationBuilder.build())
    }
}