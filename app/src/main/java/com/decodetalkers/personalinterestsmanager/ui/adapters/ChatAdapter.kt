package com.decodetalkers.personalinterestsmanager.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.decodetalkers.personalinterestsmanager.R
import com.decodetalkers.personalinterestsmanager.databinding.ContainerSendMessageBinding
import com.decodetalkers.personalinterestsmanager.models.ChatMessageModel
import kotlinx.android.synthetic.main.container_received_message.view.*
import kotlinx.android.synthetic.main.container_send_message.view.*

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    final val VIEW_TYPE_SENT = 1
    final val VIEW_TYPE_RECEIVED = 2
    val chatMessageList = arrayListOf<ChatMessageModel>()

    class ChatSentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(msg: ChatMessageModel) {
            itemView.chatSentText.text = msg.Message
        }
    }

    class ChatReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(msg: ChatMessageModel) {
            itemView.chatReceivedText.text = msg.Message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            ChatSentMessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.container_send_message, parent, false)
            )
        } else {
            ChatReceivedMessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.container_received_message, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            (holder as ChatSentMessageViewHolder).setData(chatMessageList.get(position))
        } else {
            (holder as ChatReceivedMessageViewHolder).setData(chatMessageList.get(position))
        }
    }

    override fun getItemCount(): Int {
        return chatMessageList.size
    }

    fun addNewMessage(msg: ChatMessageModel){
        chatMessageList.add(msg)
        notifyItemInserted(chatMessageList.size-1)
    }

    override fun getItemViewType(position: Int): Int {
        if (chatMessageList.get(position).isChatbotMessage) {
            return VIEW_TYPE_RECEIVED
        } else {
            return VIEW_TYPE_SENT
        }
    }
}