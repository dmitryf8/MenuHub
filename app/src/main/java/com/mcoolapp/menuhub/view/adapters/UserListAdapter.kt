package com.mcoolapp.menuhub.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.model.user.User


class UserListItemsAdapter (private var items: List<User>, private val listener: ClickListener, private val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<UserListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_list_item_view, parent, false)

        val holder = UserListViewHolder(view, context)

        holder.itemView.setOnClickListener { listener.onItemClicked(items[holder.adapterPosition]) }

        return holder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(viewHolder: UserListViewHolder, position: Int) {
        viewHolder.bind(items[position])
    }

    fun showList(list: List<User>) {
        items = list
        notifyDataSetChanged()
    }

    interface ClickListener {
        fun onItemClicked(item: User)
    }
}