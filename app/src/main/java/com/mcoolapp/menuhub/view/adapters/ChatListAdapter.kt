package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.ChatItemLayoutBinding
import com.mcoolapp.menuhub.fragments.ChatFragment
import com.mcoolapp.menuhub.model.chat.ChatData
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.repository.chatrepository.ChatRepository
import com.mcoolapp.menuhub.model.menu.Table
import com.mcoolapp.menuhub.repository.userrepository.UserRepository

class ChatListAdapter(private var items: List<ChatData?>, val navController: NavController) :
    androidx.recyclerview.widget.RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {


    class ChatListViewHolder(private val itemBinding: ChatItemLayoutBinding, val navController: NavController) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemBinding.root) {


        fun bind(chatData: ChatData) {
            println(chatData.chatName)
            itemBinding.chatData = chatData
            itemBinding.chatItemLinearLayout.setOnClickListener {
                val b = Bundle()
                b.putString(ChatFragment.CHAT_ID, chatData.chatID)
                navController.navigate(R.id.action_chatListFragment_to_chatFragment, b)
            }



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val itemBinding =
            ChatItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ChatListViewHolder(itemBinding, navController)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        val chatData = items[position]
        holder.bind(chatData!!)

    }
}
