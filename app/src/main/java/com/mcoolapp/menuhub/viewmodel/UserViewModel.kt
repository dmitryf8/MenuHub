package com.mcoolapp.menuhub.viewmodel

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.mcoolapp.menuhub.model.image.ImageWithBucket
import com.mcoolapp.menuhub.model.user.User
import com.mcoolapp.menuhub.model.user.UserAndImagePathUri
import com.mcoolapp.menuhub.repository.userrepository.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class UserViewModel : ViewModel() {
    private var userName = MutableLiveData<String>()
    private var userUserName = MutableLiveData<String>()
    private var aboutUser = MutableLiveData<String>()
    private var userFollowsNumber = MutableLiveData<String>()
    private var userFollowersNumber = MutableLiveData<String>()
    private var userPostNumber = MutableLiveData<String>()
    private var userID = MutableLiveData<String>()
    private var userPhotoId = MutableLiveData<String>()
    private var userPhotoPathUri = MutableLiveData<Uri>()
    private var progressBarVisibility = MutableLiveData<Int>()
    private var editUserButtonVisibility = MutableLiveData<Int>()
    private val userRepo = UserRepository()
    private var user = MutableLiveData<User>()
    private var userAndImagePathUri = MutableLiveData<UserAndImagePathUri>()
    private var userPhotoWithBucket = MutableLiveData<ImageWithBucket>()
    private var userMenuID = MutableLiveData<String>()

    var userViewModelListener: UserViewModelListener? = null



    init {
        progressBarVisibility.value = View.VISIBLE

    }

    fun getUserMenuID(): MutableLiveData<String> {
        return userMenuID
    }

    fun getUserPhotoWithBucket(): MutableLiveData<ImageWithBucket> {
        println("fun getUserPhotoWithBucket(): MutableLiveData<ImageWithBucket> in userviewmodel")
        //userPhotoWithBucket.value = ImageWithBucket(userPhotoId.value, "bucket" + userID.value)
        return userPhotoWithBucket
    }

    fun getUserAndImagePathUri(): MutableLiveData<UserAndImagePathUri> {
        if (userPhotoPathUri.value != null)
            userAndImagePathUri.value =
                UserAndImagePathUri(getUser().value!!, userPhotoPathUri.value!!)
        return userAndImagePathUri
    }

    public fun getEditUserButtonVisibility(): MutableLiveData<Int> {
        return editUserButtonVisibility
    }

    public fun getAboutUser(): MutableLiveData<String> {
        return aboutUser
    }

    public fun getUserName(): MutableLiveData<String> {
        return userName
    }

    public fun getUserUserName(): MutableLiveData<String> {
        return userUserName
    }

    public fun getUserFollowsNumber(): MutableLiveData<String> {
        return userFollowsNumber
    }

    public fun getUserFollowersNumber(): MutableLiveData<String> {
        return userFollowersNumber
    }

    public fun getUserPostNumber(): MutableLiveData<String> {
        return userPostNumber
    }

    public fun getUserPhotoId(): MutableLiveData<String> {
        return userPhotoId
    }

    public fun getProgressBarVisibility(): MutableLiveData<Int> {
        return progressBarVisibility
    }



    public fun getUser(): MutableLiveData<User> {
        return user
    }


    fun bind(us: User) {

        System.out.println("bind(user) user.username = " + us.userName)
        userName.value = us.name
        aboutUser.value = us.aboutUser
        userUserName.value = us.userName
        userFollowsNumber.value = us.observableIDList.size.toString()
        userFollowersNumber.value = us.subscribersIDList.size.toString()
        userPostNumber.value = us.userPostIDList.size.toString()
        userID.value = us.id
        userPhotoId.value = us.userPhotoId
        userPhotoWithBucket.value = ImageWithBucket(us.userPhotoId, "bucket" + us.id)
        userMenuID.value = us.userMenuID

        user.value = us


        System.out.println("userusername----------->" + userUserName.value)
        System.out.println("userPhotoId.value----------->" + userPhotoId.value)
        System.out.println("userName.value----------->" + userName.value)
        System.out.println("userID.value----------->" + userID.value)

    }

    fun getUserID(): MutableLiveData<String> {
        return userID
    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    lateinit var context: Context

    fun setBaseContext(context: Context) {
        System.out.println("setBaseContext in userviewmodel")
        this.context = context
        userRepo.setBaseContext(context)
    }

    @SuppressWarnings("CheckResult")
    fun loadUser(id: String) {
        progressBarVisibility.value = View.VISIBLE
        //userRepo.setBaseContext(context)
        System.out.println("loadUser in UserViewModel id = " + id)
        userRepo.getUser(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                System.out.println("loadUser in UserViewModel, List<User> = " + it)
                if (it != null)
                    bind(it)

            }, {
                progressBarVisibility.value = View.GONE
                System.out.println("loadUser error in UserViewModel -> " + it)

            }, {
                progressBarVisibility.value = View.INVISIBLE
            })
            .addTo(disposable)

    }



    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    fun saveUser() {

        if (userID.value != null) {
            System.out.println("userID.value != null in userViewModel.saveUser() = " + userID.value)
            val user = getUser().value

            user!!.userPhotoId = userPhotoId.value!!
            user.aboutUser = aboutUser.value!!
            user.userName = userUserName.value!!
            user.updatedTimeStamp = Timestamp.now()
            user.name = userName.value!!

            progressBarVisibility.value = View.VISIBLE
            userRepo.createOrUpdateUser(user!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    System.out.println("user saved with documentId = " + it)

                    userViewModelListener!!.onSavingUserDataEnd()
                }, {
                    System.out.println("user not saved with error = " + it.message)

                    Toast.makeText(context, "User saving error", Toast.LENGTH_LONG).show()
                }, {
                    progressBarVisibility.value = View.INVISIBLE
                })
                .addTo(disposable)
        }
        else {
            System.out.println("fun saveUser() user not saved  ")
        }
    }

    fun newUserPhotoId(imageId: String?) {
        getUserPhotoId().value = imageId
        val imageWithBucket = ImageWithBucket(imageId, getUserPhotoWithBucket().value!!.bucketName)
        getUserPhotoWithBucket().value = imageWithBucket
    }
    fun onClick(){
        System.out.println("click")
        //loadUser(userID.value!!)
    }

    public fun changeProgressbarVisiblity(vis: Int) {
        progressBarVisibility.postValue(vis)
    }

    interface UserViewModelListener {
        fun onSavingUserDataEnd()
    }

    fun setOnUserViewModelListener(userViewModelListener: UserViewModelListener) {
        this.userViewModelListener = userViewModelListener
    }
}