package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcoolapp.menuhub.databinding.MenuItemEditRecyclerViewBinding
import com.mcoolapp.menuhub.databinding.MenuItemsRecyclerViewBinding
import com.mcoolapp.menuhub.model.menu.MenuItem

class MenuEditViewPagerAdapter(val itemList: List<List<MenuItem?>>, private val listener: MenuItemsListMenuEditAdapter.MenuEditAdapterListener): RecyclerView.Adapter<MenuEditViewPagerViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuEditViewPagerViewHolder {
        context = parent.context
        val itemBinding = MenuItemEditRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuEditViewPagerViewHolder(itemBinding, listener)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MenuEditViewPagerViewHolder, position: Int) {
        holder.bind(itemList.get(position))
    }


}
class MenuEditViewPagerViewHolder(private val binding: MenuItemEditRecyclerViewBinding, private val listener: MenuItemsListMenuEditAdapter.MenuEditAdapterListener):
    androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    fun bind(itemsList: List<MenuItem?>){
        val context = binding.root.context
        binding.menuItemsEditListRecyclerView.setHasFixedSize(true)
        binding.menuItemsEditListRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.menuItemsEditListRecyclerView.isNestedScrollingEnabled = false
        binding.menuItemsEditListRecyclerView.adapter = MenuItemsListMenuEditAdapter(itemsList, listener)
    }
}