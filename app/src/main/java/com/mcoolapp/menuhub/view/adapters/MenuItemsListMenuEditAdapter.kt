package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.MenuItemEditLayoutBinding
import com.mcoolapp.menuhub.databinding.MenuItemLayoutBinding
import com.mcoolapp.menuhub.model.menu.MenuItem

class MenuItemsListMenuEditAdapter(private var items: List<MenuItem?>, private var listener: MenuEditAdapterListener) :
    androidx.recyclerview.widget.RecyclerView.Adapter<MenuItemsListMenuEditAdapter.MenuItemsListMenuEditViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemsListMenuEditViewHolder {
        println("override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)")
        val itemBinding =
            MenuItemEditLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)




        return MenuItemsListMenuEditViewHolder(itemBinding, listener)
    }

    override fun getItemCount(): Int {
        println("override fun getItemCount() = " + items.size )
        return items.size
    }

    override fun onBindViewHolder(holder: MenuItemsListMenuEditViewHolder, position: Int) {
        println("override fun onBindViewHolder(holder: MenuItemsListViewHolder, position: Int)")
        val menuItem = items[position]
        holder.bind(menuItem!!)
    }

    class MenuItemsListMenuEditViewHolder(private val itemBinding: MenuItemEditLayoutBinding, private val listener: MenuEditAdapterListener) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemBinding.root) {


        fun bind(menuItem: MenuItem) {
            if (menuItem.visible!!) {
                itemBinding.menuItemVisibilityButton.setBackgroundResource(R.drawable.ic_baseline_visibility_24)
            } else {
                itemBinding.menuItemVisibilityButton.setBackgroundResource(R.drawable.ic_baseline_visibility_off_24)
            }
            itemBinding.menuItem = menuItem

            itemBinding.menuItemEditButton.setOnClickListener {
                println("edit menuitem " + menuItem.id)
                listener.editMenuItem(menuItem)
            }
            itemBinding.menuItemVisibilityButton.setOnClickListener {
                listener.invertMenuItemVisibility(menuItem)

                println("visibility menuitem " + menuItem.id)

                if (menuItem.visible!!) {
                    (it as Button).setBackgroundResource(R.drawable.ic_baseline_visibility_24)
                } else {
                    (it as Button).setBackgroundResource(R.drawable.ic_baseline_visibility_off_24)
                }





            }
            itemBinding.menuItemDeleteButton.setOnClickListener {
                println("delete menuitem " + menuItem.id)
                listener.deleteMenuItem(menuItem)
            }
        }
    }

    interface MenuEditAdapterListener {
        fun editMenuItem(menuItem: MenuItem)
        fun invertMenuItemVisibility(menuItem: MenuItem)
        fun deleteMenuItem(menuItem: MenuItem)
    }

    fun setMenuEditAdapterListener(listener: MenuEditAdapterListener)
    {

    }
}