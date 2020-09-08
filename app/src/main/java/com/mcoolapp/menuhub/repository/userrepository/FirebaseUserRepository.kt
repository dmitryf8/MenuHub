package com.mcoolapp.menuhub.repository.userrepository


import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.mcoolapp.menuhub.model.menu.Menu
import com.mcoolapp.menuhub.model.user.User
import com.mcoolapp.menuhub.repository.DataBase
import com.mcoolapp.menuhub.repository.RxFirebaseStorage
import com.mcoolapp.menuhub.repository.menurepository.FirebaseMenuRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class FirebaseUserRepository {

    var context: Context? = null
    private var dataBase = DataBase.INSTANCE

    companion object {
        private const val USER_COLLECTION = "Users"
    }


    private val remoteDB = FirebaseFirestore.getInstance().apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    fun getUserList( idList: List<String>): Observable<List<User>?>{
        return  Observable.create() {emitter ->
            System.out.println("getUserList(id) id = " + idList)
            val list = arrayListOf<User>()
            remoteDB.collection(USER_COLLECTION)
                .whereIn("id", idList)
                .get()
                .addOnSuccessListener {
                    val docs = it.documents
                    for (doc in docs) {
                        val user = User(doc.data!!)
                        println(doc.data)
                        list.add(user)

                    }
                    emitter.onNext(list)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    System.out.println("FUR on error: " +it.toString())
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
        }
    }


    fun getUser(id: String): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getUserVersion(id: String): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getUserCommunicationData(id: String): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun createUser(user: User): Observable<DocumentReference?> {
        return Observable.create() { emitter ->
            remoteDB.collection(USER_COLLECTION)
                .add(user.toHashMapForCreate())
                .addOnSuccessListener {
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

    fun updateUser(user: User): Observable<Boolean?> {
        return Observable.create { emitter ->

            remoteDB.collection(USER_COLLECTION)
                .whereEqualTo("id", user.id)
                .get()
                .addOnCompleteListener {
                    if (it.result!!.documents.size >0)
                    remoteDB.collection(USER_COLLECTION)
                        .document(it.result!!.documents[0].id)
                        .update(user.toHashMapForUpdate())
                        .addOnSuccessListener {
                            if (!emitter.isDisposed) emitter.onNext(true)
                            if (!emitter.isDisposed) emitter.onComplete()
                        }
                        .addOnFailureListener {
                            emitter.onError(it.fillInStackTrace())
                            System.out.println("user " + user.id + " was not updated with error " + it.message)
                            if (!emitter.isDisposed) emitter.onComplete()
                        }


                }
                .addOnFailureListener {
                    emitter.onNext(false)
                    if (!emitter.isDisposed) emitter.onError(it.fillInStackTrace())
                    System.out.println(" Error updateUser" + it)
                }

        }
    }

    fun isUserExistsInFBDB(id: String) : Observable<Boolean> {
        return Observable.create{emitter ->
            remoteDB.collection(USER_COLLECTION)
                .whereEqualTo("id", id)
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty) {emitter.onNext(false)} else {emitter.onNext(true)}
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onNext(false)
                    emitter.onError(it.fillInStackTrace())
                    if (!emitter.isDisposed) emitter.onComplete()
                }

        }
    }

    fun saveUserCommunicationData(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}