package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.ChatItemLayoutBinding
import com.mcoolapp.menuhub.databinding.MessageItemLayoutBinding
import com.mcoolapp.menuhub.fragments.UserDetailFragment
import com.mcoolapp.menuhub.model.chat.ChatConstants
import com.mcoolapp.menuhub.model.chat.ChatData
import com.mcoolapp.menuhub.model.chat.MessageData

class ChatAdapter (private var items: List<MessageData?>, val navController: NavController) :
androidx.recyclerview.widget.RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    fun addMessageData(messageData: MessageData) {
        (items as ArrayList<MessageData?>).add(0, messageData)
        notifyDataSetChanged()
    }
    class ChatViewHolder(private val itemBinding: MessageItemLayoutBinding, val navController: NavController) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemBinding.root) {

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid


        fun bind(messageData: MessageData) {
            println(messageData.messageContent)

            if (messageData.messageType == ChatConstants.MESSAGE_CONTENT_TEXT) {
                itemBinding.imageLayoutMessage.visibility = View.GONE
                itemBinding.messageData = messageData
                if (messageData.senderID.equals(currentUserID)) {
                    itemBinding.messageImageView.visibility = View.GONE
                    itemBinding.messageTextLayout.setBackgroundResource(R.drawable.my_message_background_shape)
                    itemBinding.messageItemLinearLayout.gravity = Gravity.RIGHT
                } else {
                    itemBinding.messageImageView.visibility = View.VISIBLE
                    itemBinding.messageTextLayout.setBackgroundResource(R.drawable.another_user_message_background_shape)
                    itemBinding.messageItemLinearLayout.gravity = Gravity.LEFT
                }
            }
            itemBinding.messageImageView.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(UserDetailFragment.USER_ID_KEY, messageData.senderID)
                navController.navigate(R.id.action_chatFragment_to_userDetailFragment42, bundle )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val itemBinding =
            MessageItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ChatViewHolder(itemBinding, navController)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val messageData = items[position]
        holder.bind(messageData!!)


    }
}
