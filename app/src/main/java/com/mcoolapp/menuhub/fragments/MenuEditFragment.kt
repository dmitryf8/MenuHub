package com.mcoolapp.menuhub.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.FragmentMenuBinding
import com.mcoolapp.menuhub.databinding.FragmentMenuEditBinding
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.MenuEditViewPagerAdapter
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.MenuItemsListMenuEditAdapter
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.MenuViewPagerAdapter
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.viewmodel.MenuViewModel
import com.mcoolapp.menuhub.model.menu.MenuItem
import com.mcoolapp.menuhub.viewmodel.MenuEditViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_user.*
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.fragment_menu_edit.*
import kotlinx.android.synthetic.main.fragment_menu_edit.view.*


class MenuEditFragment : Fragment() , MenuItemsListMenuEditAdapter.MenuEditAdapterListener{
    private lateinit var menuID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private val menuViewModel by viewModels<MenuEditViewModel>()
    private lateinit var binding: FragmentMenuEditBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        menuViewModel.setBaseContext(activity?.baseContext!!)

        arguments?.let {
            menuID = it.getString(MenuFragment.MENU_ID)!!
        }


        binding = FragmentMenuEditBinding.inflate(layoutInflater).apply {
            viewmodel = menuViewModel
            lifecycleOwner = this@MenuEditFragment
        }

        System.out.println("menuID = " + menuID)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setTitleText(requireContext().getString(R.string.edit_menu))

        menuViewModel.loadMenu(menuID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                menuViewModel.getSectionItemsList().observeForever {
                    println("sectionItemsList in MenuFragment = " +menuViewModel.getSectionItemsList().value!!)
                    viewPagerMenuEditFragment.adapter = MenuEditViewPagerAdapter(menuViewModel.getSectionItemsList().value!!, this)

                    TabLayoutMediator(menuEditTabLayout, viewPagerMenuEditFragment) {tab, position ->
                        tab.text = menuViewModel.getSectionTabNameList().value!![position]
                    }.attach()
                }

                println("menuViewModel.loadMenu(menuID) titletext -->" + it)
            }, {
                println("menuViewModel.loadMenu(menuID) error -->" + it)
            }).addTo(disposable)

        addMenuItemToSectionButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(MenuFragment.MENU_ID, menuViewModel.getMenuID().value)
            bundle.putString(MenuItemEditFragment.MENU_ITEM_SECTION_NAME,
                menuEditTabLayout.getTabAt(menuEditTabLayout.selectedTabPosition)!!.text.toString())
            findNavController().navigate(R.id.action_menuEditFragment_to_menuItemEditFragment, bundle)
        }
        editTableListButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(MenuFragment.MENU_ID, menuViewModel.getMenuID().value)

            findNavController().navigate(R.id.action_menuEditFragment_to_tableListEditFragment, bundle)
        }



        menuEditTabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val text = tab!!.text
                //menuViewModel.showSection(text.toString())
            }

        })

    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuEditFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    override fun editMenuItem(menuItem: MenuItem) {
        val bundle = Bundle()
        bundle.putString(MenuFragment.MENU_ITEM_ID, menuItem.id)
        findNavController().navigate(R.id.action_menuEditFragment_to_menuItemEditFragment, bundle)

    }

    override fun invertMenuItemVisibility(menuItem: MenuItem) {
        menuItem.visible = !menuItem.visible!!
        menuViewModel.updateMenuItem(menuItem)
    }

    override fun deleteMenuItem(menuItem: MenuItem) {

    }
}