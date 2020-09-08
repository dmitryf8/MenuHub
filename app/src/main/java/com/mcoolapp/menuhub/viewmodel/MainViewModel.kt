package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.viewmodel

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.repository.chatrepository.ChatRepository
import com.mcoolapp.menuhub.repository.userrepository.UserRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel(){

    private var backArrowButtonVisibility = MutableLiveData<Int>()
    private var cartVisibility = MutableLiveData<Int>()
    private var homeFragmentContainer = MutableLiveData<Fragment>()

    private var searchMenuVisibility = MutableLiveData<Int>()
    private var imOnTheSpotMenuVisibility = MutableLiveData<Int>()
    private var settingsMenuVisibility = MutableLiveData<Int>()

    var mainViewModelListener: MainViewModelListener? = null

    private val chatRepository = ChatRepository()



    init {
        backArrowButtonVisibility.value = View.GONE
        cartVisibility.value = View.GONE
        searchMenuVisibility.value = View.GONE
        imOnTheSpotMenuVisibility.value = View.GONE
        settingsMenuVisibility.value = View.GONE
    }

    fun getBackArrowButtonVisibility(): MutableLiveData<Int> {
        return backArrowButtonVisibility
    }

    fun getHomeFragmentContainer(): MutableLiveData<Fragment> {
        return homeFragmentContainer
    }

    fun getCartVisibility(): MutableLiveData<Int> {
        return cartVisibility
    }

    fun getSearchMenuVisibility(): MutableLiveData<Int> {
        return searchMenuVisibility
    }

    fun getImOnTheSpotVisibility(): MutableLiveData<Int> {
        return imOnTheSpotMenuVisibility
    }

    fun getSettingsMenuVisibility(): MutableLiveData<Int> {
        return settingsMenuVisibility
    }

    fun onSearchMenuClick() {
        if (searchMenuVisibility.value == View.GONE) {
            searchMenuVisibility.value = View.VISIBLE
        } else {
            searchMenuVisibility.value = View.GONE
        }
        imOnTheSpotMenuVisibility.value = View.GONE
        settingsMenuVisibility.value = View.GONE
    }
    fun onImOnTheSpotMenuClick() {
        if (imOnTheSpotMenuVisibility.value == View.GONE) {
            imOnTheSpotMenuVisibility.value = View.VISIBLE
        } else {
            imOnTheSpotMenuVisibility.value = View.GONE
        }
        searchMenuVisibility.value = View.GONE
        settingsMenuVisibility.value = View.GONE
    }
    fun onSettingsMenuClick() {
        if (settingsMenuVisibility.value == View.GONE) {
            settingsMenuVisibility.value = View.VISIBLE
        } else {
            settingsMenuVisibility.value = View.GONE
        }
        imOnTheSpotMenuVisibility.value = View.GONE
        searchMenuVisibility.value = View.GONE
    }

    interface MainViewModelListener {
        fun onShowUser(id:String?)
    }

    fun setOnMainViewModelListener(mainViewModelListener: MainViewModelListener?) {
        this.mainViewModelListener = mainViewModelListener
    }

    fun onClick(v:View){
        System.out.println("click")
    }
    private lateinit var context: Context

    fun setContext(context: Context) {
        this.context = context
        chatRepository.setContext(context)
        //chatRepository.subscribeToChats(FirebaseAuth.getInstance().currentUser!!.uid)

    }

    fun sendRegistrationToServer(token: String) {
        val userRepository = UserRepository()
        userRepository.setBaseContext(context)
        var userID = ""
        if (FirebaseAuth.getInstance().currentUser != null)
            userID = FirebaseAuth.getInstance().currentUser!!.uid
        println("userID in MainViewModel sendRegistrationToServer -> " + userID)



        userRepository.getUser(userID)
            .subscribeOn(Schedulers.io())
            .subscribe({
                it!!.fcmToken = token
                userRepository.updateUser(it)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        println("user.fcmToken updated")
                    }, {
                        println("error in MHubFCMService updateUser -> " + it.message)
                    })
            }, {
                println("error in MHubFCMService getUser -> " + it.message)
            })
            .addTo(disposable)
    }
    val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }


    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}