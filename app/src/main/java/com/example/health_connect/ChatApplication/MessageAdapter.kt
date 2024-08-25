package com.example.health_connect

import android.content.Context
import android.view.LayoutInflater
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ITEM_SENT = 2
    val ITEM_RECIEVE = 1

    class SentViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val sentmessage = itemview.findViewById<TextView>(R.id.sent_mssg)
        val sent_time = itemview.findViewById<TextView>(R.id.time_sent)
        val sent_date = itemview.findViewById<TextView>(R.id.date_sent)
    }

    class RecieveViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val recievedmessage = itemview.findViewById<TextView>(R.id.recieved_mssg)
        val recieve_time = itemview.findViewById<TextView>(R.id.time_recieve)
        val recieve_date = itemview.findViewById<TextView>(R.id.date_recieve)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view = LayoutInflater.from(context).inflate(R.layout.received, parent, false)
            return RecieveViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {

        var currentmessage = messageList[position]
        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentmessage.senderId))
            return ITEM_SENT
        else
            return ITEM_RECIEVE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentmessage = messageList[position]
        if (holder.javaClass == SentViewHolder::class.java) {

            val viewHolder = holder as SentViewHolder
            holder.apply {
                sentmessage.text = currentmessage.message
                sent_time.text = currentmessage.time
                sent_date.text = currentmessage.date
            }
        } else {
            val viewHolder = holder as RecieveViewHolder
            holder.apply {
                recievedmessage.text = currentmessage.message
                recieve_time.text = currentmessage.time
                recieve_date.text  = currentmessage.date

            }
        }
    }
}