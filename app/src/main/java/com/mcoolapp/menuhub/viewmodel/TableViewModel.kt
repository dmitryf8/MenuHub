package com.mcoolapp.menuhub.viewmodel

import android.content.Context
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mcoolapp.menuhub.model.menu.Table
import com.mcoolapp.menuhub.repository.menurepository.MenuRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class TableViewModel: ViewModel(), LifecycleOwner {

    private var progressBarVisibility = MutableLiveData<Int>()
    private var menuID = MutableLiveData<String>()
    private var tableList = MutableLiveData<List<Table>>()

    fun getPrograssbarVisibility(): MutableLiveData<Int> {
        return  progressBarVisibility
    }
    fun getMenuID(): MutableLiveData<String> {
        return  menuID
    }
    fun getTableList(): MutableLiveData<List<Table>> {
        return  tableList
    }

    private val menuRepository = MenuRepository()

    private lateinit var context: Context

    init {
        progressBarVisibility.value = View.GONE
    }

    fun setMenuID(id: String) {
        menuID.value = id
        menuRepository.getMenu(id)
            .subscribeOn(Schedulers.io())
            .subscribe({
                menuRepository.getTableList(it!!.tableIDList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        tableList.value = it!!
                    }, {
                        println("error in TableViewModel.setMenuId -> " + it.message)
                    })
                    .addTo(disposable)
            }, {
                println("error in setMenuID in TableViewModel -> " + it.message)
            })
            .addTo(disposable)

    }

    fun setBaseContext(context: Context) {
        System.out.println("setBaseContext in tableviewmodel")
        this.context = context
        menuRepository.setBaseContext(context)
    }


    override fun getLifecycle(): Lifecycle {
        TODO("Not yet implemented")
    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}