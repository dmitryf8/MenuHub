package com.mcoolapp.menuhub.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mcoolapp.menuhub.R
import com.mcoolapp.menuhub.model.image.ImageWithBucket
import com.mcoolapp.menuhub.model.menu.Menu
import com.mcoolapp.menuhub.model.menu.MenuItem
import com.mcoolapp.menuhub.repository.menurepository.MenuRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MenuItemViewModel : ViewModel(), LifecycleOwner {

    private var id = MutableLiveData<String>()
    private var itemName = MutableLiveData<String>()
    private var ingredients = MutableLiveData<List<String>>()
    private var aboutItem = MutableLiveData<String>()
    private var weight = MutableLiveData<Double>()
    private var weightUnit = MutableLiveData<String>()
    private var price = MutableLiveData<Double>()
    private var priceUnit = MutableLiveData<String>()
    private var photoId = MutableLiveData<ImageWithBucket>()
    private var photoUri = MutableLiveData<Uri>()
    private var menuItemsIdsList = MutableLiveData<List<String>>()
    private var progressBarVisibility = MutableLiveData<Int>()
    private var communicationPartId = MutableLiveData<String>()
    private var ownerId = MutableLiveData<String>()
    private var ownerName = MutableLiveData<String>()
    private var parentID = MutableLiveData<String>()
    private var sectionName = MutableLiveData<String>()
    private val menuRepository = MenuRepository()

    private lateinit var context: Context

    val ingredientArray: Array<String> = arrayOf("Креветки", "Черника", "Хамон")

    val priceMenu = R.menu.menu_item_price_unit_menu
    val weightMenu = R.menu.menu_item_weight_unit_menu

    init {
        progressBarVisibility.value = View.GONE
        ingredients.value = listOf()
        bind(MenuItem())

    }

    fun getSectionName(): MutableLiveData<String> {
        return sectionName
    }

    fun getParentId(): MutableLiveData<String> {
        return parentID
    }

    fun setImageUri(uri: Uri) {
        photoUri.value = uri
    }

    fun setContext(context: Context) {
        menuRepository.setBaseContext(context)
    }

    fun bind(menuItem: MenuItem) {
        println("bind menuItem.ownerId = " + menuItem.ownerId)
        id.value = menuItem.id
        itemName.value = menuItem.itemName
        ingredients.value = menuItem.ingredients
        aboutItem.value = menuItem.aboutItem
        weight.value = menuItem.weight
        weightUnit.value = menuItem.weightUnit
        price.value = menuItem.price
        priceUnit.value = menuItem.priceUnit
        photoId.value = ImageWithBucket(menuItem.photoId, "bucket" + menuItem.ownerId)
        communicationPartId.value = menuItem.communicationPartId
        ownerId.value = menuItem.ownerId
        ownerName.value = menuItem.ownerName
        sectionName.value = menuItem.sectionName


    }


    fun addMenuItem(): Observable<Boolean> {
        return Observable.create {emitter ->
            val menuItem: MenuItem = MenuItem()
            System.out.println("itemName.value " + itemName.value)
            menuItem.itemName = itemName.value
            System.out.println("ingredients.value " + ingredients.value)
            menuItem.ingredients = ingredients.value

            System.out.println("aboutItem.value " + aboutItem.value)
            menuItem.aboutItem = aboutItem.value
            System.out.println("weight.value " + weight.value)
            menuItem.weight = weight.value ?: 0.0
            System.out.println("weightUnit.value" + weightUnit.value)
            menuItem.weightUnit = weightUnit.value
            System.out.println("price.value" + price.value)
            menuItem.price = price.value ?: 0.0
            System.out.println("priceUnit.value" + priceUnit.value)
            menuItem.priceUnit = priceUnit.value
            System.out.println("communicationPartId.value" + communicationPartId.value)
            menuItem.communicationPartId = ""
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            System.out.println("userId" + userId)
            menuItem.ownerId = userId
            System.out.println("menuItem.version = " + menuItem.version)
            menuItem.ownerName = ownerName.value
            menuItem.sectionName = sectionName.value

            //progressBarVisibility.value = View.VISIBLE

            menuRepository.createMenuItem(menuItem)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    menuItem.id = it
                    menuRepository.saveMenuItemForMenu(parentID.value!!, sectionName.value, it)
                        .subscribeOn(Schedulers.io())
                        .subscribe({

                            System.out.println("menuitem saved for menu")
                            menuRepository.updateMenuItem(menuItem)
                                .subscribeOn(Schedulers.io())
                                .subscribe({
                                    progressBarVisibility.value = View.GONE
                                    emitter.onNext(true)
                                    println("menuItem " +menuItem.id + " up to date")
                                }, {emitter.onError(it)
                                    progressBarVisibility.value = View.GONE
                                    if (!emitter.isDisposed) emitter.onComplete()
                                    println("error in MenuItemViewModel -> " + it.stackTrace)
                                })
                        }, {
                            emitter.onError(it)
                            progressBarVisibility.value = View.GONE
                            if (!emitter.isDisposed) emitter.onComplete()
                            System.out.println(
                                "is Menu -> {\n" +
                                        "                        menuRepository.saveMenuItemForMenu error = " + it
                            )
                        })

                }, {
                    emitter.onError(it)
                    //progressBarVisibility.value = View.GONE
                    if (!emitter.isDisposed) emitter.onComplete()
                    System.out.println("menuRepository.createMenuItem(menuItem) error = " + it)
                }).addTo(disposable)
        }

    }

    fun setParentID(id: String) {
        parentID.value = id
    }

    fun setSectionName(s: String) {
        sectionName.value = s
    }

    fun updateMenuItem(): Observable<Boolean>{
        return Observable.create {emitter ->
            val menuItem: MenuItem = MenuItem()
            menuItem.id = id.value!!
            System.out.println("itemName.value " + itemName.value)
            menuItem.itemName = itemName.value
            System.out.println("ingredients.value " + ingredients.value)
            menuItem.ingredients = ingredients.value
            menuItem.photoId = photoId.value!!.imageId

            System.out.println("aboutItem.value " + aboutItem.value)
            menuItem.aboutItem = aboutItem.value
            System.out.println("weight.value " + weight.value)
            menuItem.weight = weight.value ?: 0.0
            System.out.println("weightUnit.value" + weightUnit.value)
            menuItem.weightUnit = weightUnit.value
            System.out.println("price.value" + price.value)
            menuItem.price = price.value ?: 0.0
            System.out.println("priceUnit.value" + priceUnit.value)
            menuItem.priceUnit = priceUnit.value
            System.out.println("communicationPartId.value" + communicationPartId.value)
            menuItem.communicationPartId = ""
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            System.out.println("userId" + userId)
            menuItem.ownerId = userId
            System.out.println("menuItem.version = " + menuItem.version)
            menuItem.ownerName = ownerName.value
            menuItem.sectionName = sectionName.value

            //progressBarVisibility.value = View.VISIBLE

            menuRepository.updateMenuItem(menuItem)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    emitter.onNext(true)
                    if (!emitter.isDisposed) emitter.onComplete()
                }, {
                    emitter.onError(it)
                    println("error in MenuItemViewModel updateMenuItem -> " +it.stackTrace.contentToString())
                    if (!emitter.isDisposed) emitter.onComplete()
                }).addTo(disposable)
        }

    }

    fun saveMenuItem(): Observable<Boolean> {
        return Observable.create {emitter->
            println("menuItem id.value = " + id.value)
            if (id.value != null && id.value != "") {
                updateMenuItem()
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        emitter.onNext(it)
                        if (!emitter.isDisposed) emitter.onComplete()
                    }, {
                        emitter.onError(it)
                        if (!emitter.isDisposed) emitter.onComplete()
                    })
                    .addTo(disposable)
            } else {
                addMenuItem()
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        emitter.onNext(it)
                        if (!emitter.isDisposed) emitter.onComplete()
                    }, {
                        emitter.onError(it)
                        if (!emitter.isDisposed) emitter.onComplete()
                    })
                    .addTo(disposable)
            }
        }
    }

    fun setMenuItemImageId (id: String) {
        println("bucketName in fun setMenuItemImageId (id: String) -------------->" + "bucket"+ownerId.value)
        photoId.value = ImageWithBucket(id, "bucket"+ownerId.value)
    }

    fun loadMenuItem(id: String) {

            progressBarVisibility.value = View.VISIBLE

            menuRepository.getMenuItem(listOf(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    progressBarVisibility.value = View.GONE
                    val menuItem = it.get(0)
                    println("menuItem.ItemName in loadMenuItem MenuItem ViewModel = " + menuItem!!.itemName)
                    bind(menuItem)
                    println("menuItem.ItemName in loadMenuItem MenuItem ViewModel = " + menuItem.itemName)


                }, {
                    progressBarVisibility.value = View.GONE
                    println("error in MenuItemViewModel loadMenuItem -> " + it.stackTrace!!.contentToString())

                })
                .addTo(disposable)


    }

    public fun getId(): MutableLiveData<String> {
        return id
    }

    public fun getItemName(): MutableLiveData<String> {
        return itemName
    }

    public fun getIngredients(): MutableLiveData<List<String>> {
        return ingredients
    }

    public fun getWeight(): MutableLiveData<Double> {
        return weight
    }

    public fun getWeightUnit(): MutableLiveData<String> {
        return weightUnit
    }

    public fun getPrice(): MutableLiveData<Double> {
        return price
    }

    public fun getPriceUnit(): MutableLiveData<String> {
        return priceUnit
    }

    public fun getPhotoId(): MutableLiveData<ImageWithBucket> {
        return photoId
    }

    public fun getCommunicationPartId(): MutableLiveData<String> {
        return communicationPartId
    }

    public fun getPhotoUri(): MutableLiveData<Uri> {
        return photoUri
    }

    public fun getMenuItemsIdsList(): MutableLiveData<List<String>> {
        return menuItemsIdsList
    }

    public fun getProgressBarVisibility(): MutableLiveData<Int> {
        return progressBarVisibility
    }

    public fun getOwnerNamme(): MutableLiveData<String> {
        return ownerName
    }

    public fun getAboutItem(): MutableLiveData<String> {
        return aboutItem
    }

    public fun getOwnerId(): MutableLiveData<String> {
        return ownerId
    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    override fun getLifecycle(): Lifecycle {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}