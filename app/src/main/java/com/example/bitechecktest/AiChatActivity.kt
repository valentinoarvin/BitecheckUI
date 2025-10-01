package com.example.bitechecktest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitechecktest.databinding.ActivityAiChatBinding

class AiChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiChatBinding
    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messageList)
        binding.recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(this@AiChatActivity)
            adapter = chatAdapter
        }
    }

    private fun sendMessage() {
        val messageText = binding.etChatInput.text.toString()
        if (messageText.isNotBlank()) {
            messageList.add(ChatMessage(messageText, Sender.USER))

            messageList.add(ChatMessage("Placeholder AI response", Sender.AI))

            chatAdapter.notifyItemRangeInserted(messageList.size - 2, 2)

            binding.recyclerViewChat.scrollToPosition(messageList.size - 1)

            binding.etChatInput.text.clear()
        }
    }
}