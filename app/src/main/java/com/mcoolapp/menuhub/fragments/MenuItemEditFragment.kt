package com.mcoolapp.menuhub.fragments

import android.content.Context
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
import com.mcoolapp.menuhub.databinding.FragmentMenuEditBinding
import com.mcoolapp.menuhub.databinding.FragmentMenuItemEditBinding
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.MainActivity
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.view.adapters.MenuEditViewPagerAdapter
import com.mcoolapp.menuhub.viewmodel.MenuEditViewModel
import com.mcoolapp.menuhub.viewmodel.MenuItemViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_menu_edit.*


class MenuItemEditFragment : Fragment(), MainActivity.MenuItemImageIDListener{
    private var menuItemID: String? = null
    private var sectionName: String? = null
    private var menuID: String? = null

    private val menuItemViewModel by viewModels<MenuItemViewModel>()
    private lateinit var binding: FragmentMenuItemEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(MenuFragment.MENU_ITEM_ID)){
                menuItemID = it.getString(MenuFragment.MENU_ITEM_ID)!!
                println("menuItemID = "+ menuItemID)

            } else {
                sectionName = it.getString(MENU_ITEM_SECTION_NAME)
                menuID = it.getString(MenuFragment.MENU_ID)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        menuItemViewModel.setContext(activity?.baseContext!!)

        binding = FragmentMenuItemEditBinding.inflate(layoutInflater).apply {
            viewmodel = menuItemViewModel
            lifecycleOwner = this@MenuItemEditFragment
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setTitleText(requireContext().getString(R.string.edit_menu))
        (activity as MainActivity).imageIdListener = this

        if (menuItemID != null) {
            menuItemViewModel.loadMenuItem(menuItemID!!)

        } else {
            menuItemViewModel.setParentID(menuID!!)
            menuItemViewModel.setSectionName(sectionName!!)
        }

        binding.saveMenuItemButton.setOnClickListener {
            menuItemViewModel.saveMenuItem()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    findNavController().navigateUp()
                }, {
                    println("error in MenuItemEditFragment saveMenuItem ->" + it.stackTrace!!.contentToString())
                })
                .addTo(disposable)
        }

        binding.menuItemImageView.setOnClickListener {
            (activity as MainActivity).chooseMenuItemImage()
        }

    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    companion object {
        const val MENU_ITEM_SECTION_NAME = "sdkfjnkflkdxmvlkxdgsllksdmld;lzf"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuItemEditFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }


    override fun imageID(id: String) {
        menuItemViewModel.setMenuItemImageId(id)
    }
}