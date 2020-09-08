package com.mcoolapp.menuhub.repository.userrepository

import android.content.Context
import androidx.room.Database
import com.mcoolapp.menuhub.model.menu.Menu
import com.mcoolapp.menuhub.model.user.User
import com.mcoolapp.menuhub.model.user.UserDao
import com.mcoolapp.menuhub.repository.DataBase
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class UserRepository {

    private val firebaseUserRepository: FirebaseUserRepository = FirebaseUserRepository()
    private var dataBase = DataBase.INSTANCE

    var context: Context? = null

    fun setBaseContext(cont: Context) {
        context = cont
        firebaseUserRepository.context = context
        dataBase = DataBase.getAppDataBase(context!!)
    }


    fun getUser(id: String): Observable<User?> {

        System.out.println(" fun getUser(id: String): Observable<User?> id = " + id)
        return Observable.create() { emitter ->
            val user = dataBase!!.userDao().getUser(id)

            if (user != null) {
                println("iiiiiiissssssss eeeeeeeeexxxxxxxiiiiiiiiisssssssstttttttttt iiiiiiiiinnnnnnnrrrrooooooooooommmmmmmmm")
                dataBase = DataBase.getAppDataBase(context!!)

                emitter.onNext(user)

                firebaseUserRepository.getUserList(listOf(id))
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        if (it!!.size > 0)
                            emitter.onNext(it.get(0)) else emitter.onError(Throwable("User data download error in FB"))
                        updateInRoom(it.get(0))
                        if (!emitter.isDisposed) emitter.onComplete()
                    }, {
                        println(
                            "----------------------------------------UserRepository -> getUser(" + id + ") error!!!!-------------------------------------------------" +
                                    "-" + it.message
                        )
                    }).addTo(disposable)
                if (!emitter.isDisposed) emitter.onComplete()
            } else {
                firebaseUserRepository.getUserList(listOf(id))
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        if (it!!.size > 0)
                            emitter.onNext(it.get(0)) else emitter.onError(Throwable("User data download error in FB"))
                        saveInRoom(it.get(0))
                        if (!emitter.isDisposed) emitter.onComplete()
                    }, {
                        System.out.println(
                            "----------------------------------------UserRepository -> getUser(" + id + ") error!!!!-------------------------------------------------" +
                                    "-" + it.message
                        )
                    }).addTo(disposable)
            }
        }

    }

    fun getUserList(idList: List<String>): Observable<List<User>?> {

        System.out.println(" fun getUserList(idList): Observable<List<User?>> idlist = " + idList)
        return Observable.create() { emitter ->
            firebaseUserRepository.getUserList(idList)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (it!!.size > 0) {

                        emitter.onNext(it)
                    } else {
                        println("emitter.onError(Throwable(\"User data download error in FB\")")
                        emitter.onError(Throwable("User data download error in FB"))
                    }
                    if (!emitter.isDisposed) emitter.onComplete()
                }, {
                    println(
                        "----------------------------------------UserRepository -> getUserList error!!!!-------------------------------------------------" +
                                "-" + it.message
                    )
                    if (!emitter.isDisposed) emitter.onComplete()
                }).addTo(disposable)


        }

    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }


    fun createOrUpdateUser(user: User): Observable<String> {
        return Observable.create { em ->
            firebaseUserRepository.isUserExistsInFBDB(user.id)
                .subscribeOn(Schedulers.io())
                .subscribe({

                    if (it) {
                        updateUser(user)
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                em.onNext(user.id)

                                if (!em.isDisposed) em.onComplete()
                            }, {
                                if (!em.isDisposed) em.onError(it)
                            })


                    } else {
                        System.out.println("user not exists in firebase database")
                        createUser(user)
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                em.onNext(it)
                                if (!em.isDisposed) em.onComplete()
                            }, {
                                em.onError(it)
                            })

                    }
                }, {
                    em.onError(it)
                    if (!em.isDisposed) em.onComplete()
                })

        }
    }

    fun createUser(user: User): Observable<String> {
        return Observable.create { emitter ->

            firebaseUserRepository.createUser(user)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val id = it!!.id
                    emitter.onNext(id)
                    saveInRoom(user)
                    if (!emitter.isDisposed) emitter.onComplete()
                }, {
                    emitter.onError(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                })


        }
    }

    fun updateUser(user: User): Observable<Boolean> {
        return Observable.create { emitter ->

            firebaseUserRepository.updateUser(user = user)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    emitter.onNext(it!!)
                    updateInRoom(user)
                    if (!emitter.isDisposed) emitter.onComplete()
                }, {
                    if (!emitter.isDisposed) emitter.onError(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                })

        }
    }


    private fun isExistsInRoom(id: String): Observable<Boolean?> {
        return Observable.create { emitter ->
            val isExists = dataBase!!.userDao().isExist(id)
            emitter.onNext(isExists)
            if (!emitter.isDisposed) emitter.onComplete()

        }
    }

    private fun saveInRoom(user: User) {
        var job: Job = Job()

        job = GlobalScope.launch(Dispatchers.IO) {
            System.out.println(context)

            dataBase!!.userDao().insert(user)
        }

        job.start()
    }

    private fun updateInRoom(user: User) {
        var job: Job = Job()
        job = GlobalScope.launch(Dispatchers.IO) {

            dataBase!!.userDao().update(user)
        }

        job.start()
    }
}