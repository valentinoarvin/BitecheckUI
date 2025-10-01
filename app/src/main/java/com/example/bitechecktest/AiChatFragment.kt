package com.example.bitechecktest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitechecktest.databinding.FragmentAiChatBinding

class AiChatFragment : Fragment() {

    // --- View Binding Setup ---
    private var _binding: FragmentAiChatBinding? = null
    private val binding get() = _binding!!

    // --- Chat Properties ---
    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    // This function is where all the setup happens. It's crucial.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "AI Chat"

        // Call the functions to bring the UI to life
        setupRecyclerView()

        binding.btnSend.setOnClickListener {
            sendMessage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // --- Helper Functions ---

    // This function connects your RecyclerView to the ChatAdapter
    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messageList)
        binding.recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }
    }

    // This function handles the logic for the send button
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