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

    private var _binding: FragmentAiChatBinding? = null
    private val binding get() = _binding!!

    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "AI Chat"

        setupRecyclerView()

        binding.btnSend.setOnClickListener {
            sendMessage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messageList)
        binding.recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(requireContext())
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