package com.mcoolapp.menuhub.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.FragmentMenuBinding
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.MenuItemsListAdapter
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.MenuViewPagerAdapter
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.viewmodel.MenuViewModel
import com.mcoolapp.menuhub.view.UserEditActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.user_detail_fragment.*


class MenuFragment : Fragment() {

    private var menuID: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private val menuViewModel by viewModels<MenuViewModel>()
    private lateinit var binding: FragmentMenuBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        menuViewModel.setBaseContext(activity?.baseContext!!)

        arguments?.let {
            menuID = it.getString(MENU_ID)!!
            System.out.println("menuID = " + menuID)
        }


        binding = FragmentMenuBinding.inflate(layoutInflater).apply {
            viewmodel = menuViewModel
            lifecycleOwner = this@MenuFragment
        }




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("super.onViewCreated(view, savedInstanceState)")

        menuViewModel.loadMenu(menuID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                (activity as MainActivity).setTitleText(it!!)

                menuViewModel.getSectionItemsList().observeForever {
                    println("sectionItemsList in MenuFragment = " +menuViewModel.getSectionItemsList().value!!)
                    viewPagerMenuFragment.adapter = MenuViewPagerAdapter(menuViewModel.getSectionItemsList().value!!)

                    TabLayoutMediator(menuTabLayout, viewPagerMenuFragment) {tab, position ->
                        tab.text = menuViewModel.getSectionTabNameList().value!![position]
                    }.attach()
                }


                println("menuViewModel.loadMenu(menuID) titletext -->" + it)
            }, {
                println("menuViewModel.loadMenu(menuID) error -->" + it)
            }).addTo(disposable)



    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    companion object {
        public const val MENU_ID = "jgnvdjifsfgrsdlsdmzc,vnu83249"
        public const val MENU_ITEM_ID = "sldjgshfjosejfeskgsrkjngkdldkd"
        @JvmStatic
        fun newInstance() =
            MenuFragment().apply {
                arguments = Bundle().apply {
                    menuID = this.getString(MENU_ID)!!
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        editMenuItemButtonMenuFragment.setOnClickListener {
            val bundle = Bundle()
            println("editMenuItemButtonMenuFragment.setOnClickListener = ")
            bundle.putString(MenuFragment.MENU_ID, menuViewModel.getMenuID().value)
            findNavController().navigate(R.id.action_menuFragment_to_menuEditFragment, bundle)
        }
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }
}