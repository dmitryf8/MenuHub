package com.mcoolapp.menuhub.viewmodel

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mcoolapp.menuhub.model.menu.Menu
import com.mcoolapp.menuhub.model.menu.MenuItem
import com.mcoolapp.menuhub.model.user.User
import com.mcoolapp.menuhub.repository.menurepository.MenuRepository
import com.mcoolapp.menuhub.repository.userrepository.UserRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MenuEditViewModel: ViewModel(), LifecycleOwner {
    private var menuID = MutableLiveData<String>()
    private var menuItemsIdList = MutableLiveData<List<String>>()
    private var menuOwnerID = MutableLiveData<String>()
    private var progressBarVisibility = MutableLiveData<Int>()
    private var menuOwnerName = MutableLiveData<String>()
    private var sectionTabNameList = MutableLiveData<List<String>>()
    private var sectionItemsList = MutableLiveData<List<List<MenuItem?>>>()

    private val userRepository = UserRepository()
    private val menuRepository = MenuRepository()

    private lateinit var context: Context

    init {
        progressBarVisibility.value = View.GONE
    }

    fun getSectionItemsList(): MutableLiveData<List<List<MenuItem?>>> {
        return sectionItemsList
    }

    fun getSectionTabNameList(): MutableLiveData<List<String>> {
        return sectionTabNameList
    }

    fun getMenuID(): MutableLiveData<String> {
        return menuID
    }

    fun getMenuItemsIdList(): MutableLiveData<List<String>> {
        return menuItemsIdList
    }

    fun getMenuOwnerID(): MutableLiveData<String> {
        return menuOwnerID
    }

    fun getMenuOwnerName(): MutableLiveData<String> {
        return menuOwnerName
    }

    var menuItemList = listOf<MenuItem?>()

    private fun bind(menu: Menu, user: User) {
        menuID.value = menu.id
        menuItemsIdList.value = menu.menuItemsIdList
        menuOwnerID.value = menu.ownerId
        menuOwnerName.value = user.userName
        sectionTabNameList.value = menu.sectionList

        menuRepository.getMenuItem(menuItemsIdList.value!!)
            .subscribeOn(Schedulers.io())
            .subscribe({
                menuItemList = it
                var list = mutableListOf<List<MenuItem>>()
                println("menuItemList = it " + menuItemList)


                for ((index, value) in sectionTabNameList.value!!.withIndex()) {
                    val l = mutableListOf<MenuItem>()
                    for (item in it) {
                        if (item!!.sectionName == sectionTabNameList.value!!.get(index)  ) {
                            l.add(item)
                            println("item " + item.itemName + "added to section " + index)
                        }
                    }
                    list.add(l)
                }


                sectionItemsList.value = list
                println(sectionItemsList.value!!)
            }, {
                println("getMenuItem error in menuiewmodel --> " + it)
            }).addTo(disposable)
    }

    fun loadMenu(id: String): Observable<String> {
        progressBarVisibility.value = View.VISIBLE
        return Observable.create { emitter ->
            menuRepository.getMenu(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val menu = it!!
                    userRepository.getUser(menu.ownerId!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({

                            progressBarVisibility.value = View.INVISIBLE
                            bind(menu, it!!)
                            emitter.onNext(it.userName)
                            if (!emitter.isDisposed) emitter.onComplete()
                        }, {
                            emitter.onError(it)
                            if (!emitter.isDisposed) emitter.onComplete()
                            progressBarVisibility.value = View.INVISIBLE
                            System.out.println("MenuViewModel userRepository.getUser error: " + it)
                        })
                }, {
                    progressBarVisibility.value = View.INVISIBLE
                    System.out.println("MenuViewModel menuRepository.getMenu error: " + it)
                }).addTo(disposable)
        }

    }

    fun setBaseContext(context: Context) {
        System.out.println("setBaseContext in userviewmodel")
        this.context = context
        userRepository.setBaseContext(context)
        menuRepository.setBaseContext(context)
    }


    override fun getLifecycle(): Lifecycle {
        TODO("Not yet implemented")
    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    fun updateMenuItem(menuItem: MenuItem) {
        progressBarVisibility.value = View.VISIBLE
        menuRepository.updateMenuItem(menuItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                progressBarVisibility.value = View.GONE
            }, {
                progressBarVisibility.value = View.GONE
                println("error in menuViewModel -> " +it)
                Toast.makeText(context, "Its not impossible", Toast.LENGTH_LONG)
            }).addTo(disposable)
    }


    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}