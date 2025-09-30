package com.example.bitechecktest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messageList: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_AI = 2
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.tvChatMessage)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].sender == Sender.USER) {
            VIEW_TYPE_USER
        } else {
            VIEW_TYPE_AI
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = if (viewType == VIEW_TYPE_USER) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_user, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_ai, parent, false)
        }
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.messageText.text = messageList[position].text
    }

    override fun getItemCount(): Int = messageList.size
}