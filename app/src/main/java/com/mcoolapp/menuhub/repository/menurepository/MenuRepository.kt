package com.mcoolapp.menuhub.repository.menurepository

import android.content.Context
import com.mcoolapp.menuhub.model.menu.CommunicationPartMenuItem
import com.mcoolapp.menuhub.model.menu.Menu
import com.mcoolapp.menuhub.model.menu.MenuItem
import com.mcoolapp.menuhub.model.menu.Table
import com.mcoolapp.menuhub.model.user.UserDao
import com.mcoolapp.menuhub.repository.DataBase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class MenuRepository {

    private val firebaseMenuRepository: FirebaseMenuRepository = FirebaseMenuRepository()
    private var dataBase = DataBase.INSTANCE


    var context: Context? = null

    fun setBaseContext(context: Context) {
        this.context = context
        firebaseMenuRepository.context = context
    }


    fun getMenu(id: String): Observable<Menu?> {
        System.out.println("fun getMenu(id: String): Observable<Menu?> id = " + id)
        return Observable.create() { emitter ->

            System.out.println("return Observable.create() {emitter ->")
            dataBase = DataBase.getAppDataBase(context = context!!)
            val menu = dataBase!!.menuDao().getMenu(id)

            if (menu == null) {

                System.out.println("menu == null id = " + id)
                firebaseMenuRepository.getMenu(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        println("getMenu it.id = " + it!!.id)
                        emitter.onNext(it)

                    }, {
                        System.out.println("----------------------------------------MenuRepository -> getMenu(" + id + ") error!!!! --> " + it.message)
                    })
            } else {
                System.out.println("menu in room")
                emitter.onNext(menu)
                firebaseMenuRepository.getMenu(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        emitter.onNext(it!!)
                    }, {
                        System.out.println("----------------------------------------MenuRepository -> getMenu(" + id + ") error!!!!--------------------------------------------------")
                    })
            }
        }
    }

    fun createMenu(menu: Menu): Observable<String> {
        return Observable.create { emitter ->
            System.out.println("create Menu in MenuRepository")

            firebaseMenuRepository.createMenu(menu)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val id = it!!.id
                    emitter.onNext(id)
                    menu.id = id
                    firebaseMenuRepository.updateMenu(menu)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            println("menu with id up to date in firebase")
                        }, {
                            emitter.onError(it)
                        })
                        .addTo(disposable)
                    saveInRoom(menu)
                    if (!emitter.isDisposed) emitter.onComplete()
                }, {
                    emitter.onError(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                })

            saveInRoom(menu)

        }
    }

    fun updateMenu(menu: Menu): Observable<Boolean> {
        return Observable.create { emitter ->
            System.out.println("update Menu")

            firebaseMenuRepository.updateMenu(menu)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    emitter.onNext(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                }, {
                    emitter.onError(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                })
            updateInRoom(menu)
        }
    }

    fun getMenuItemFromMenu(menuID: String): Observable<List<MenuItem?>> {
        System.out.println("fun getMenuItem(idList: List<String>): Observable<MenuItem?> id = " + menuID)
        return Observable.create() { emitter ->

            System.out.println("return Observable.create() {emitter ->")

            firebaseMenuRepository.getMenuItemListFromMenu(menuID)
                .subscribeOn(Schedulers.io())
                // .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    println("firebaseMenuRepository.getMenuItemList -> " + it)
                    emitter.onNext(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()

                    for (menuItem in it) {
                        isExistsInRoom(menuItem)
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                if (it!!) {
                                    updateInRoom(menuItem)
                                } else {
                                    saveInRoom(menuItem)
                                }
                            }, {
                                System.out.println("Error getMenuItem(idList: List<String>) : " + it)
                                emitter.onError(it)
                            })
                            .addTo(disposable)

                    }
                }, {
                    emitter.onError(it)
                    System.out.println("----------------------------------------MenuRepository -> getMenuItems(" + it + ") error!!!!--------------------------------------------------")
                })

        }
    }

    fun getMenuItem(menuItemID: String): Observable<MenuItem?> {
        System.out.println("fun getMenuItem(idList: List<String>): Observable<MenuItem?> id = " + menuItemID)
        return Observable.create() { emitter ->

            System.out.println("return Observable.create() {emitter ->")
            dataBase = DataBase.getAppDataBase(context = context!!)
            var isExists = true
            println("var isExists = true")
            val menuItem = MenuItem()
            println("val list: ArrayList<MenuItem> = arrayListOf()")
            try {
                dataBase!!.menuItemDao().getMenuItem(menuItemID)
            } catch (e: Exception) {
                println(e.message.toString())
            }

            if (menuItem.id == "") {
                firebaseMenuRepository.getMenuItem(menuItemID)
                    .subscribeOn(Schedulers.io())
                    // .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        println("firebaseMenuRepository.getMenuItemList -> " + it)
                        emitter.onNext(it!!)
                        if (!emitter.isDisposed) emitter.onComplete()
                    }, {
                        emitter.onError(it)
                        System.out.println("----------------------------------------MenuRepository -> getMenuItems(" + it + ") error!!!!--------------------------------------------------")
                    })
            } else {
                System.out.println("menuItems in room")
                emitter.onNext(menuItem)
                if (!emitter.isDisposed) emitter.onComplete()
            }
        }
    }

    fun createMenuItem(menuItem: MenuItem?): Observable<String> {
        System.out.println("MenuRepository createMenuItem(menuItem: MenuItem?")
        return Observable.create { emitter ->
            System.out.println("create MenuItem ")
            System.out.println(menuItem!!.toHashMapForCreate())
            System.out.println("create MenuItem")
            if (menuItem == null) System.out.println("menuItem == null") else System.out.println("menuItem != null")
            firebaseMenuRepository.createMenuItem(menuItem)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    System.out.println("created menuItem ")
                    val id = it!!.id
                    System.out.println("id = " + id)
                    emitter.onNext(id)
                    menuItem.id = id
                    updateMenuItem(menuItem)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            System.out.println("updated menuItem with id ")
                        }, {
                            System.out.println(" createMenuItem error bfhftfhf:" + it)
                        })
                    saveInRoom(menuItem)
                    if (!emitter.isDisposed) emitter.onComplete()
                }, {

                    emitter.onError(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                })
        }
    }

    fun updateMenuItem(menuItem: MenuItem): Observable<Boolean> {
        return Observable.create { emitter ->
            System.out.println("update MenuItem")

            firebaseMenuRepository.updateMenuItem(menuItem)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    emitter.onNext(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                }, {
                    emitter.onError(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                })
            updateInRoom(menuItem)
        }
    }

    fun getCommunicationPartMenuItem(id: String): Observable<CommunicationPartMenuItem?> {
        System.out.println("getCommunicationPartMenuItem(id: String) id = " + id)
        return Observable.create() { emitter ->

            System.out.println("return Observable.create() {emitter ->")
            dataBase = DataBase.getAppDataBase(context = context!!)
            val isExists = dataBase!!.communicationPartMenuItemDao().isExist(id)

            if (!isExists) {

                System.out.println("menu == null")
                firebaseMenuRepository.getCommunicationPartMenuItemList(listOf(id))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        emitter.onNext(it!!.get(0))
                    }, {
                        System.out.println("----------------------------------------MenuRepository -> getcommPart(" + id + ") error!!!!--------------------------------------------------")
                    })
            } else {
                System.out.println("communicationPartMenuItemDao in room")
                emitter.onNext(dataBase!!.communicationPartMenuItemDao().getCommPartMenuItem(id))
            }
        }
    }

    fun createCommunicationPartMenuItem(communicationPartMenuItem: CommunicationPartMenuItem): Observable<String> {
        return Observable.create { emitter ->
            System.out.println("create Menu")

            firebaseMenuRepository.createCommunicationPartMenuItem(
                communicationPartMenuItem,
                context!!
            )
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val id = it!!.id
                    emitter.onNext(id)
                    communicationPartMenuItem.id = id
                    updateCommunicationPartMenuItem(communicationPartMenuItem)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            System.out.println("communicationPartMenuItem updated")
                        }, {
                            System.out.println("communicationPartMenuItem updating error: " + it)
                        })
                    saveInRoom(communicationPartMenuItem)
                    if (!emitter.isDisposed) emitter.onComplete()
                }, {
                    emitter.onError(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                })
        }
    }

    fun updateCommunicationPartMenuItem(communicationPartMenuItem: CommunicationPartMenuItem): Observable<Boolean> {
        return Observable.create { emitter ->
            System.out.println("update communicationPartMenuItem")

            firebaseMenuRepository.updateCommunicationPart(communicationPartMenuItem)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    emitter.onNext(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                }, {
                    emitter.onError(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                })
            updateInRoom(communicationPartMenuItem)
        }
    }

    fun getTableList(idList: List<String>): Observable<List<Table>> {
        return Observable.create { emitter ->
            firebaseMenuRepository.getTableList(idList)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    emitter.onNext(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }, {
                    println("error in MenuRepository.getTableList -> " + it.message)
                })
                .addTo(disposable)
        }

    }

    fun getTable(id: String): Observable<Table?> {
        System.out.println("fun getTable(id: String): Observable<Menu?> id = " + id)
        return Observable.create() { emitter ->

            System.out.println("return Observable.create() {emitter ->")
            dataBase = DataBase.getAppDataBase(context = context!!)
            val table = dataBase!!.tableDao().getTable(id)

            if (table == null) {

                System.out.println("table == null")
                firebaseMenuRepository.getTable(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        emitter.onNext(it!!)
                    }, {
                        System.out.println("----------------------------------------MenuRepository -> getTable(" + id + ") error!!!!--------------------------------------------------")
                    }).addTo(disposable)
            } else {
                System.out.println("table in room")
                emitter.onNext(table)
                firebaseMenuRepository.getTable(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        emitter.onNext(it!!)
                    }, {
                        System.out.println("----------------------------------------MenuRepository -> getTable(" + id + ") error!!!!--------------------------------------------------")
                    }).addTo(disposable)
            }
        }
    }

    fun createTable(table: Table): Observable<String> {
        return Observable.create { emitter ->
            System.out.println("create table in MenuRepository")

            firebaseMenuRepository.createTable(table)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val id = it!!.id
                    emitter.onNext(id)
                    table.id = id
                    firebaseMenuRepository.updateTable(table)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            println("table up to date with id in firebase")
                        }, {

                        })
                        .addTo(disposable)
                    saveInRoom(table)
                    if (!emitter.isDisposed) emitter.onComplete()
                }, {
                    emitter.onError(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                })


        }
    }

    fun updateTable(table: Table): Observable<Boolean> {
        return Observable.create { emitter ->
            System.out.println("update table")

            firebaseMenuRepository.updateTable(table)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    updateInRoom(table)
                    emitter.onNext(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                }, {
                    emitter.onError(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                })

        }
    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    private fun isExistsInRoom(obj: Any): Observable<Boolean?> {
        System.out.println("private fun isExists(imageWithId: ImageWithId): Observable<Boolean?>")

        return Observable.create { emitter ->
            when (obj) {
                is Menu -> {
                    val isExists = dataBase!!.menuDao().isExist((obj as Menu).id)
                    emitter.onNext(isExists)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                is MenuItem -> {
                    val isExists = dataBase!!.menuItemDao().isExist((obj as MenuItem).id)
                    emitter.onNext(isExists)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                is CommunicationPartMenuItem -> {
                    val isExists = dataBase!!.communicationPartMenuItemDao()
                        .isExist((obj as CommunicationPartMenuItem).id)
                    emitter.onNext(isExists)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                is Table -> {
                    val isExists = dataBase!!.tableDao().isExist((obj as Table).id)
                    emitter.onNext(isExists)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
            }
        }
    }

    private fun saveInRoom(obj: Any) {
        System.out.println("saveInRoom(obj: Any)")
        var job: Job = Job()
        when (obj) {
            is Menu -> {
                job = GlobalScope.launch(Dispatchers.IO) {
                    dataBase!!.menuDao().insert(obj as Menu)
                }
            }
            is MenuItem -> {
                job = GlobalScope.launch(Dispatchers.IO) {
                    if (!dataBase!!.menuItemDao().isExist((obj as MenuItem).id))
                        (obj as MenuItem) else
                        updateInRoom(obj as MenuItem)

                }
            }
            is CommunicationPartMenuItem -> {
                job = GlobalScope.launch(Dispatchers.IO) {
                    dataBase!!.communicationPartMenuItemDao()
                        .insert(obj as CommunicationPartMenuItem)
                }
            }
            is Table -> {
                job = GlobalScope.launch(Dispatchers.IO) {
                    dataBase!!.tableDao().insert(obj as Table)
                }
            }
        }

        job.start()
    }

    private fun updateInRoom(obj: Any) {
        System.out.println("private fun updateInRoom(obj: Any)")
        var job: Job = Job()
        when (obj) {
            is Menu -> {
                job = GlobalScope.launch(Dispatchers.IO) {
                    dataBase!!.menuDao().update(obj as Menu)
                }
            }
            is MenuItem -> {
                job = GlobalScope.launch(Dispatchers.IO) {
                    dataBase!!.menuItemDao().update(obj as MenuItem)
                }
            }
            is CommunicationPartMenuItem -> {
                job = GlobalScope.launch(Dispatchers.IO) {
                    dataBase!!.communicationPartMenuItemDao()
                        .update(obj as CommunicationPartMenuItem)
                }
            }
            is Table -> {
                job = GlobalScope.launch(Dispatchers.IO) {
                    dataBase!!.tableDao().update(obj as Table)
                }
            }
        }
        job.start()
    }


    fun saveMenuItemForMenu(
        menuID: String,
        sectionName: String?,
        menuItemId: String?
    ): Observable<Boolean?> {
        return Observable.create { emitter ->
            getMenu(menuID)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (!it!!.sectionList!!.contains(sectionName!!)) {
                        it.sectionList = it.sectionList!!.plusElement(sectionName)
                    }
                    (it.menuItemsIdList as ArrayList<String>).add(menuItemId!!)
                    updateMenu(it)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            emitter.onNext(true)
                            if (!emitter.isDisposed) emitter.onComplete()
                        }, {
                            emitter.onError(it)
                            if (!emitter.isDisposed) emitter.onComplete()
                        })
                }, {
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                    System.out.println("fun saveMenuItemForMenu error = " + it)
                })
                .addTo(disposable)
        }
    }

    fun saveTableForMenu(menuID: String, tableID: String?): Observable<Boolean?> {
        return Observable.create { emitter ->
            getMenu(menuID)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    (it!!.tableIDList as ArrayList<String>).add(tableID!!)
                    updateMenu(it)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            emitter.onNext(true)
                            if (!emitter.isDisposed) emitter.onComplete()
                        }, {
                            emitter.onError(it)
                            if (!emitter.isDisposed) emitter.onComplete()
                        })
                }, {
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                    System.out.println("fun saveTableForMenu error = " + it)
                })
                .addTo(disposable)
        }
    }
}