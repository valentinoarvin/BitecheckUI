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

        // Set up the close button to finish the activity
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Set up the send button
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
            // 1. Add user's message to the list
            messageList.add(ChatMessage(messageText, Sender.USER))

            // 2. Add the hardcoded AI response
            messageList.add(ChatMessage("Placeholder AI response", Sender.AI))

            // 3. Notify the adapter that two new items were added
            chatAdapter.notifyItemRangeInserted(messageList.size - 2, 2)

            // 4. Scroll to the new message
            binding.recyclerViewChat.scrollToPosition(messageList.size - 1)

            // 5. Clear the input box
            binding.etChatInput.text.clear()
        }
    }
}