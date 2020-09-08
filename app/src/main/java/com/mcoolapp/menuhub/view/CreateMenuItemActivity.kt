package com.mcoolapp.menuhub.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.databinding.ActivityEditMenuItemBinding
import com.mcoolapp.menuhub.model.menu.Menu
import com.mcoolapp.menuhub.utils.isConnectedToNetwork
import com.mcoolapp.menuhub.viewmodel.MenuItemViewModel
import io.reactivex.disposables.CompositeDisposable

class CreateMenuItemActivity : AppCompatActivity(), LifecycleOwner {

    val disposable = CompositeDisposable()
    private lateinit var binding: ActivityEditMenuItemBinding
    private lateinit var viewModel: MenuItemViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_menu_item)

        if (isConnectedToNetwork()) {
            System.out.println("Internet connection alive")
        } else {
            System.out.println("Internet connection dead")
        }

        viewModel = ViewModelProviders.of(this).get(MenuItemViewModel::class.java)
        binding.setLifecycleOwner(this)
        binding.viewmodel = viewModel
        viewModel.setContext(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.setImageUri(data!!.data!!)
    }

    val IMAGE_REQUEST_CODE = 537

    fun chooseImage(v: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("image/*")
        ActivityCompat.startActivityForResult(this, intent, IMAGE_REQUEST_CODE, null)
    }

    fun saveMenuItemTest(v: View) {
        val menu = Menu()
        menu.id = "usYw8jFDLjttSPG0PyYQ"
        menu.version = "1"
    }
}
