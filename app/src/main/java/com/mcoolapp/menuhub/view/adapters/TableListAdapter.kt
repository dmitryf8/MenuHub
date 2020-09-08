package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.room.R
import com.mcoolapp.menuhub.databinding.MenuItemLayoutBinding
import com.mcoolapp.menuhub.databinding.TableItemLayoutBinding
import com.mcoolapp.menuhub.fragments.MenuFragment
import com.mcoolapp.menuhub.fragments.TableEditFragment
import com.mcoolapp.menuhub.model.menu.MenuItem
import com.mcoolapp.menuhub.model.menu.Table

class TableListAdapter(private var items: List<Table?>, val navController: NavController, val menuID: String) :
    androidx.recyclerview.widget.RecyclerView.Adapter<TableListAdapter.TableListViewHolder>() {

    class TableListViewHolder(private val itemBinding: TableItemLayoutBinding, val navController: NavController, val menuID: String) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemBinding.root) {


        fun bind(table: Table) {
            println(table.tableName)

            itemBinding.table = table
            itemBinding.editTableInTableItemLayoutButton.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(TableEditFragment.TABLE_ID, table.id)
                bundle.putString(MenuFragment.MENU_ID, menuID)
                navController.navigate(com.mcoolapp.menuhub.R.id.action_tableListEditFragment_to_tableEditFragment, bundle)

            }

            itemBinding.showQRCodeForTable.setOnClickListener {
                val bundle  = Bundle()
                bundle.putString(TableEditFragment.BUCKET_NAME, "bucket" + table.ownerId)
                bundle.putString(TableEditFragment.IMAGE_ID, table.qrCodeImageId)
                navController.navigate(com.mcoolapp.menuhub.R.id.action_tableListEditFragment_to_imageFragment, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableListViewHolder {
        val itemBinding =
            TableItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TableListViewHolder(itemBinding, navController, menuID)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: TableListViewHolder, position: Int) {
        val table = items[position]
        holder.bind(table!!)

    }
}