package com.mcoolapp.menuhub.fragments

import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.FragmentChatBinding
import com.mcoolapp.menuhub.databinding.FragmentChatListBinding
import com.mcoolapp.menuhub.model.chat.ChatConstants
import com.mcoolapp.menuhub.model.chat.MessageData
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.model.chat.Message
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.ChatAdapter
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.ChatListAdapter
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.MenuItemsListAdapter
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.MenuViewPagerAdapter
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.viewmodel.ChatViewModel
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.viewmodel.MenuViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_chat.*
import java.security.Key


class ChatFragment : Fragment() {
    private var chatID: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private val chatViewModel by viewModels<ChatViewModel>()
    private lateinit var binding: FragmentChatBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        chatViewModel.setBaseContext(activity?.baseContext!!)

        arguments?.let {
            if (it.containsKey(CHAT_ID)) {
                chatID = it.getString(CHAT_ID)!!
            }
        }


        binding = FragmentChatBinding.inflate(layoutInflater).apply {
            viewmodel = chatViewModel
            lifecycleOwner = this@ChatFragment
        }

        binding.chatRecyclerView.setHasFixedSize(false)
        binding.chatRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        binding.chatRecyclerView.isNestedScrollingEnabled = false

        chatViewModel.getMessageDataList().observeForever {
            println("observe list message size = " + it.size)
            binding.chatRecyclerView.adapter = ChatAdapter(it!!, findNavController())
        }
        chatViewModel.setMessageDataList(chatID)

        val onKeyListener = View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                val text = binding.inputMessageText.text.toString()
                binding.inputMessageText.setText("")
                if (!text.equals("")) {
                    val message = Message()
                    val messageData = MessageData()
                    message.messageType = ChatConstants.MESSAGE_CONTENT_TEXT
                    message.messageStatus = ChatConstants.STATUS_MESSAGE_CREATED
                    message.messageContent = text
                    val currUserID = FirebaseAuth.getInstance().currentUser!!.uid
                    message.senderID = currUserID
                    message.addUserListToList(chatViewModel.chat.userIDList)
                    message.description = text
                    message.senderName = chatViewModel.getUserName().value!!
                    message.timestamp = Timestamp.now()
                    messageData.senderID = currUserID
                    messageData.senderName = message.senderName
                    messageData.messageType = ChatConstants.MESSAGE_CONTENT_TEXT
                    messageData.messageStatus = ChatConstants.STATUS_MESSAGE_CREATED
                    messageData.messageContent = text
                    chatViewModel.addMessageData(messageData)

                    chatViewModel.sendMessage(message)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if (it) {
                                println("message sended")
                            }
                        }, {
                            println("sendMessage error -> " + it.stackTrace.contentToString())
                        })
                        .addTo(disposable)
                }
            }

            println("keycode " + keyCode)

            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().navigateUp()
            }

            return@OnKeyListener true
        }
        binding.inputMessageText.setOnKeyListener(onKeyListener)

        binding.sendTextButton.setOnClickListener {
            println("send Click")
            val text = binding.inputMessageText.text.toString()
            binding.inputMessageText.setText("")
            if (!text.equals("")) {
                val message = Message()
                val messageData = MessageData()
                message.messageType = ChatConstants.MESSAGE_CONTENT_TEXT
                message.messageStatus = ChatConstants.STATUS_MESSAGE_CREATED
                message.messageContent = text
                val currUserID = FirebaseAuth.getInstance().currentUser!!.uid
                message.senderID = currUserID
                message.senderName = chatViewModel.getUserName().value!!
                message.addUserListToList(chatViewModel.chat.userIDList)
                message.description = text
                message.timestamp = Timestamp.now()
                messageData.senderID = currUserID
                messageData.senderName = message.senderName
                messageData.messageType = ChatConstants.MESSAGE_CONTENT_TEXT
                messageData.messageStatus = ChatConstants.STATUS_MESSAGE_CREATED
                messageData.messageContent = text

                chatViewModel.addMessageData(messageData)

                chatViewModel.sendMessage(message)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it) {
                            println("message sended")
                        }
                    }, {
                        println("sendMessage error -> " + it.stackTrace.contentToString())
                    })
                    .addTo(disposable)
            }
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("super.onViewCreated(view, savedInstanceState)")
        chatViewModel.getUserName().observeForever {
            (activity as MainActivity).setTitleText(it)
        }

    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    override fun onStop() {
        super.onStop()

    }

    companion object {

        const val CHAT_ID: String = "lkdfsdnvksjlefksi7ry4788thbgsfkslvn,jfbnxojg8rht7wojhks"

        @JvmStatic

        fun newInstance() =
            MenuFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }
}