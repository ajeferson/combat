package br.com.ajeferson.combat.view.view.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.ajeferson.combat.R
import br.com.ajeferson.combat.databinding.RvItemChatBinding
import br.com.ajeferson.combat.view.service.model.ChatMessage

/**
 * Created by ajeferson on 25/02/2018.
 */
class ChatRecyclerViewAdapter: RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val binding = DataBindingUtil.inflate<RvItemChatBinding>(inflater, R.layout.rv_item_chat, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(messages[position])

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: RvItemChatBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(chatMessage: ChatMessage) {
            binding.message = chatMessage
            binding.executePendingBindings()
        }

    }

}