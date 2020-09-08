package com.mcoolapp.menuhub.repository.menurepository

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.mcoolapp.menuhub.model.menu.CommunicationPartMenuItem
import com.mcoolapp.menuhub.model.menu.Menu
import com.mcoolapp.menuhub.model.menu.MenuItem
import com.mcoolapp.menuhub.model.menu.Table
import com.mcoolapp.menuhub.repository.DataBase
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class FirebaseMenuRepository {

    var context: Context? = null
    private var dataBase = DataBase.INSTANCE

    fun updateMenu(menu: Menu): Observable<Boolean?> {
        return Observable.create { emitter ->

            remoteDB.collection(MENU_COLLECTION)
                .document(menu.id)
                .update(menu.toHashMapForUpdate())
                .addOnCompleteListener {
                    if (!emitter.isDisposed) emitter.onNext(true)
                    System.out.println(" SUCCESS override fun updateMenu")

                }
                .addOnFailureListener {
                    emitter.onNext(false)
                    if (!emitter.isDisposed) emitter.onError(it.fillInStackTrace())
                    System.out.println(" Error override fun updateMenu" + it)
                }

        }
    }

    fun updateTable(table: Table): Observable<Boolean?> {
        return Observable.create { emitter ->

            remoteDB.collection(TABLE_COLLECTION)
                .document(table.id)
                .update(table.toHashMapForUpdate())
                .addOnCompleteListener {
                    if (!emitter.isDisposed) emitter.onNext(true)
                    System.out.println(" SUCCESS override fun updateTable")

                }
                .addOnFailureListener {
                    emitter.onNext(false)
                    if (!emitter.isDisposed) emitter.onError(it.fillInStackTrace())
                    System.out.println(" Error override fun updateTable" + it)
                }

        }
    }

    fun updateMenuItem(menuItem: MenuItem): Observable<Boolean?> {
        return Observable.create { emitter ->

            remoteDB.collection(MENU_ITEMS_COLLECTION)
                .document(menuItem.id)
                .update(menuItem.toHashMapForUpdate())
                .addOnCompleteListener {
                    if (!emitter.isDisposed) emitter.onNext(true)
                    System.out.println(" SUCCESS override fun updateMenuItem")

                }
                .addOnFailureListener {
                    emitter.onNext(false)
                    if (!emitter.isDisposed) emitter.onError(it)
                    System.out.println(" Error override fun updateMenuItem" + it)
                }

        }
    }

    fun updateCommunicationPart(communicationPartMenuItem: CommunicationPartMenuItem): Observable<Boolean?> {
        return Observable.create { emitter ->

            remoteDB.collection(COMMUNICATION_PART_MENU_ITEM)
                .document(communicationPartMenuItem.id)
                .update(communicationPartMenuItem.toHashMapForUpdate())
                .addOnCompleteListener {
                    if (!emitter.isDisposed) emitter.onNext(true)
                    System.out.println(" SUCCESS override fun updateCommunicationPart")

                }
                .addOnFailureListener {
                    emitter.onNext(false)
                    if (!emitter.isDisposed) emitter.onError(it)
                    System.out.println(" Error override fun updateCommunicationPart" + it)
                }

        }
    }


    companion object {
        private const val MENU_COLLECTION = "menu"
        private const val MENU_ITEMS_COLLECTION = "menu_items"
        private const val COMMUNICATION_PART_MENU_ITEM = "communication_part_menu_item"
        private const val TABLE_COLLECTION = "table"


    }


    private val remoteDB = FirebaseFirestore.getInstance().apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }


    fun isMenuExist(id: String, context: Context): Observable<List<DocumentSnapshot>>? {
        System.out.println("fun isMenuExist(id: String, context: Context): Observable<List<DocumentSnapshot>>?")
        return Observable.create() { emitter ->
            remoteDB.collection(MENU_COLLECTION)
                .whereEqualTo("id", id)
                .get()
                .addOnSuccessListener {
                    System.out.println(" onSucces fun isMenuExist(id: String, context: Context): Observable<List<DocumentSnapshot>>?")

                    emitter.onNext(it.documents)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {

                    System.out.println("Error  fun isMenuExist(id: String, context: Context): Observable<List<DocumentSnapshot>>?")
                    if (!emitter.isDisposed) emitter.onError(it)
                }

        }

    }

    fun isMenuItemExist(id: String, context: Context): Observable<List<DocumentSnapshot>>? {
        System.out.println("fun isMenuExist(id: String, context: Context): Observable<List<DocumentSnapshot>>?")
        return Observable.create() { emitter ->
            remoteDB.collection(MENU_ITEMS_COLLECTION)
                .whereEqualTo("id", id)
                .get()
                .addOnSuccessListener {
                    System.out.println(" onSucces fun isMenuItemsExist(id: String, context: Context): Observable<List<DocumentSnapshot>>?")

                    emitter.onNext(it.documents)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {

                    System.out.println("Error  fun isMenuItemsExist(id: String, context: Context): Observable<List<DocumentSnapshot>>?")
                    if (!emitter.isDisposed) emitter.onError(it)
                }

        }

    }

    fun getMenu(id: String): Observable<Menu?> {
        return Observable.create() { emitter ->
            System.out.println("getMenu(id) id = " + id)
            remoteDB.collection(MENU_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener {
                    val doc = it
                    println(doc.data)
                    val menu = Menu(doc.data!!)

                    emitter.onNext(menu)

                    val job: Job = GlobalScope.launch(Dispatchers.IO) {
                        dataBase = DataBase.getAppDataBase(context = context!!)
                        if (dataBase!!.menuDao().isExist(id))
                            dataBase!!.menuDao().update(menu)
                        else {
                            println("!!!!!!!!!!!!!menu not exist in room")
                            //dataBase!!.menuDao().insert(menu)
                        }
                        System.out.println("-------------------------Menu saved in room---------------------------------------")
                    }
                    job.start()
                    updateMenu(menu)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    System.out.println("menu saving error: " + it.toString())
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
        }
    }

    fun getTableList(idList: List<String>): Observable<List<Table>> {
        return Observable.create() {emitter ->
            remoteDB.collection(TABLE_COLLECTION)
                .whereIn("id", idList)
                .get()
                .addOnSuccessListener{
                    val list = arrayListOf<Table>()
                    val docs = it.documents
                    for (doc in docs) {
                        list.add(Table(doc.data!!))
                    }
                    emitter.onNext(list)

                    if (!emitter.isDisposed) emitter.onComplete()

                }
                .addOnFailureListener {
                    println("error in FireBaseMenuRepository.getTableList(" + idList +") -> " + it.message)
                }

        }
    }

    fun getTable(id: String): Observable<Table?> {
        return Observable.create() { emitter ->
            System.out.println("getTable(id) id = " + id)
            remoteDB.collection(TABLE_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener {
                    val doc = it
                    println(doc.data)
                    val table = Table(doc.data!!)

                    emitter.onNext(table)

                    /**val job: Job = GlobalScope.launch(Dispatchers.IO) {
                        dataBase = DataBase.getAppDataBase(context = context!!)
                        val t = dataBase!!.tableDao().getTable(id)
                        if (t != null){
                            println("!!!!!!!!!!!!!table exist in room")
                            dataBase!!.tableDao().update(table)}
                        else {
                            println("!!!!!!!!!!!!!table not exist in room")
                            dataBase!!.tableDao().insert(table)
                        }
                        System.out.println("-------------------------Table saved in room---------------------------------------")
                    }
                    job.start()**/
                    //updateTable(table)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    System.out.println("table saving error: " + it.toString())
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
        }
    }


    fun getMenuItemList(idList: List<String>): Observable<List<MenuItem>?> {
        return Observable.create() { emitter ->
            System.out.println("!!!!!!!!!!!!!!!!!!!!getMenuItemList(idList: List<String>): Observable<List<MenuItem>?> id = " + idList)
            val list = arrayListOf<MenuItem>()
            remoteDB.collection(MENU_ITEMS_COLLECTION)
                .whereIn("id", idList)
                .get()
                .addOnSuccessListener {
                    val docs = it.documents
                    for (doc in docs) {

                        val menuItem = MenuItem(doc.data!!)
                        println("-------------->>> menuItem.itemName = " + menuItem.itemName)
                        list.add(menuItem)

                        /** val job: Job = GlobalScope.launch(Dispatchers.IO) {
                        dataBase = DataBase.getAppDataBase(context = context!!)
                        if (!dataBase!!.menuItemDao().isExist(menuItem.id))
                        dataBase!!.menuItemDao().insert(menuItem) else
                        dataBase!!.menuItemDao().update(menuItem)
                        System.out.println("-------------------------MenuItem saved in room---------------------------------------")
                        }
                        job.start()**/

                    }
                    emitter.onNext(list)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    System.out.println("FMR on error: " + it.toString())
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
        }
    }

    fun getCommunicationPartMenuItemList(idList: List<String>): Observable<List<CommunicationPartMenuItem>?> {
        return Observable.create() { emitter ->
            System.out.println(
                "getCommunicationPartMenuItemList(idList: List<String>): Observable<List<MenuItem>?> id = " + idList.get(
                    0
                )
            )
            val list = arrayListOf<CommunicationPartMenuItem>()
            remoteDB.collection(COMMUNICATION_PART_MENU_ITEM)
                .whereIn("id", idList)
                .get()
                .addOnSuccessListener {
                    val docs = it.documents
                    for (doc in docs) {
                        val commPartMenuItem = CommunicationPartMenuItem(doc.data!!)
                        list.add(commPartMenuItem)

                        val job: Job = GlobalScope.launch(Dispatchers.IO) {
                            dataBase = DataBase.getAppDataBase(context = context!!)
                            dataBase!!.communicationPartMenuItemDao().insert(commPartMenuItem)
                            System.out.println("-------------------------MenuItem saved in room---------------------------------------")
                        }
                        job.start()

                    }
                    emitter.onNext(list)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    System.out.println("FMR on error: " + it.toString())
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
        }
    }

    fun createMenu(menu: Menu): Observable<DocumentReference?> {
        return Observable.create() { emitter ->
            remoteDB.collection(MENU_COLLECTION)
                .add(menu.toHashMapForCreate())
                .addOnSuccessListener {
                    menu.id = it.id
                    updateMenu(menu).subscribeOn(Schedulers.io())
                        .subscribe({
                            System.out.println("menu created with id = " + menu.id)
                        },
                            { System.out.println("menu creating error with id = " + menu.id) })

                    emitter.onNext(it)

                    if (!emitter.isDisposed)
                        emitter.onComplete()
                }

                .addOnFailureListener {
                    emitter.onError(it)
                    if (!emitter.isDisposed)
                        emitter.onComplete()
                }

        }
    }

    fun createTable(table: Table): Observable<DocumentReference?> {
        return Observable.create() { emitter ->
            remoteDB.collection(TABLE_COLLECTION)
                .add(table.toHashMapForCreate())
                .addOnSuccessListener {
                    table.id = it.id
                    updateTable(table)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            System.out.println("table created with id = " + table.id)
                        },
                            { System.out.println("table creating error with id = " + table.id) })

                    emitter.onNext(it)

                    if (!emitter.isDisposed)
                        emitter.onComplete()
                }

                .addOnFailureListener {
                    emitter.onError(it)
                    if (!emitter.isDisposed)
                        emitter.onComplete()
                }

        }
    }

    fun createMenuItem(menuItem: MenuItem): Observable<DocumentReference?> {
        return Observable.create() { emitter ->
            remoteDB.collection(MENU_ITEMS_COLLECTION)
                .add(menuItem.toHashMapForCreate())
                .addOnSuccessListener {
                    menuItem.id = it.id
                    updateMenuItem(menuItem).subscribeOn(Schedulers.io())
                        .subscribe(
                            {
                                System.out.println("menuItem created with id = " + menuItem.id)
                            },
                            { System.out.println("menuItem creating error with id = " + menuItem.id) })

                    emitter.onNext(it)

                    if (!emitter.isDisposed)
                        emitter.onComplete()
                }

                .addOnFailureListener {
                    System.out.println("firebaseMenuRepository createMenuItem error = " + it.message)
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }

        }
    }

    fun createCommunicationPartMenuItem(
        communicationPartMenuItem: CommunicationPartMenuItem,
        context: Context
    ): Observable<DocumentReference?> {
        return Observable.create() { emitter ->
            remoteDB.collection(COMMUNICATION_PART_MENU_ITEM)
                .add(communicationPartMenuItem.toHashMapForCreate())
                .addOnSuccessListener {
                    communicationPartMenuItem.id = it.id
                    updateCommunicationPart(communicationPartMenuItem).subscribeOn(Schedulers.io())
                        .subscribe(
                            {
                                System.out.println("CommunicationPartMenuItem created with id = " + communicationPartMenuItem.id)
                            },
                            { System.out.println("CommunicationPartMenuItem creating error with id = " + communicationPartMenuItem.id) })

                    emitter.onNext(it)

                    if (!emitter.isDisposed)
                        emitter.onComplete()
                }

                .addOnFailureListener {
                    emitter.onError(it)
                    if (!emitter.isDisposed)
                        emitter.onComplete()
                }
        }
    }
}
