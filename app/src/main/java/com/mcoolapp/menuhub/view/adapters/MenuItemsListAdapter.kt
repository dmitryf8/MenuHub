package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.MenuItemLayoutBinding
import com.mcoolapp.menuhub.model.menu.MenuItem
import com.mcoolapp.menuhub.viewmodel.MenuItemViewModel

class MenuItemsListAdapter(private var items: List<MenuItem?>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<MenuItemsListAdapter.MenuItemsListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemsListViewHolder {
        println("override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)")
        val itemBinding =
            MenuItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return MenuItemsListViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        println("override fun getItemCount() = " + items.size )
        return items.size
    }

    override fun onBindViewHolder(holder: MenuItemsListViewHolder, position: Int) {
        println("override fun onBindViewHolder(holder: MenuItemsListViewHolder, position: Int)")
        val menuItem = items[position]
        holder.bind(menuItem!!)
    }

    class MenuItemsListViewHolder(private val itemBinding: MenuItemLayoutBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemBinding.root) {


        fun bind(menuItem: MenuItem) {
            println("fun bind(menuItem: MenuItem)")
            println(menuItem.itemName)
            itemBinding.menuItem = menuItem
        }
    }
}