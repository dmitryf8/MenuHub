package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcoolapp.menuhub.databinding.MenuItemsRecyclerViewBinding
import com.mcoolapp.menuhub.model.menu.MenuItem

class MenuViewPagerAdapter(val itemList: List<List<MenuItem?>>): RecyclerView.Adapter<MenuViewPagerViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewPagerViewHolder {
        context = parent.context
        val itemBinding = MenuItemsRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewPagerViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MenuViewPagerViewHolder, position: Int) {
        holder.bind(itemList.get(position))
    }


}
class MenuViewPagerViewHolder(private val binding: MenuItemsRecyclerViewBinding):
    androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    fun bind(itemsList: List<MenuItem?>){
        val context = binding.root.context
        binding.menuItemsListRecyclerView.setHasFixedSize(true)
        binding.menuItemsListRecyclerView.layoutManager =
            GridLayoutManager(context, 2)
        binding.menuItemsListRecyclerView.isNestedScrollingEnabled = false
        binding.menuItemsListRecyclerView.adapter = MenuItemsListAdapter(itemsList)
    }
}