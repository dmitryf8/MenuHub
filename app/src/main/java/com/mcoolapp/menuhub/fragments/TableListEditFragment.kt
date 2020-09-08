package com.mcoolapp.menuhub.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.FragmentMenuBinding
import com.mcoolapp.menuhub.databinding.FragmentTableListEditBinding
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.TableListAdapter
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.viewmodel.MenuViewModel
import com.mcoolapp.menuhub.viewmodel.TableViewModel
import kotlinx.android.synthetic.main.fragment_table_list_edit.*

class TableListEditFragment : Fragment() {
    private lateinit var menuID: String
    private val tableViewModel by viewModels<TableViewModel>()
    private lateinit var binding: FragmentTableListEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).setTitleText(getString(R.string.table_list))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tableViewModel.setBaseContext(activity?.baseContext!!)

        arguments?.let {
            menuID = it.getString(MenuFragment.MENU_ID)!!
            println(" menuID in tablelistEditFragment --> " + menuID)
        }

        binding = FragmentTableListEditBinding.inflate(layoutInflater).apply {
            viewModel = tableViewModel
            lifecycleOwner = this@TableListEditFragment
        }

        tableViewModel.getTableList().observeForever {
            val context = binding.root.context
            binding.recyclerViewInTableListEditFragmentInTableListFragment.setHasFixedSize(true)
            binding.recyclerViewInTableListEditFragmentInTableListFragment.layoutManager =
                LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            binding.recyclerViewInTableListEditFragmentInTableListFragment.isNestedScrollingEnabled = false
            println(menuID)
            binding.recyclerViewInTableListEditFragmentInTableListFragment.adapter = TableListAdapter(it, findNavController(), menuID)
        }

        System.out.println("menuID = " + menuID)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tableViewModel.setMenuID(menuID)
        addTableInTableListFragment.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(MenuFragment.MENU_ID, menuID)
            findNavController().navigate(R.id.action_tableListEditFragment_to_tableEditFragment, bundle)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TableListEditFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}