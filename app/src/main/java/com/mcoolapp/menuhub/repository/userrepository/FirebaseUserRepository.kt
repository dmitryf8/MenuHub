package com.mcoolapp.menuhub.repository.userrepository


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.room.Ignore
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.model.user.OrganizationData
import com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.model.user.UserConfidentialInfo
import com.mcoolapp.menuhub.model.menu.Menu
import com.mcoolapp.menuhub.model.post.Post
import com.mcoolapp.menuhub.model.post.PostWithCPP
import com.mcoolapp.menuhub.model.post.PostWithCPPID
import com.mcoolapp.menuhub.model.user.User
import com.mcoolapp.menuhub.repository.DataBase
import com.mcoolapp.menuhub.repository.RxFirebaseStorage
import com.mcoolapp.menuhub.repository.menurepository.FirebaseMenuRepository
import com.mcoolapp.menuhub.repository.postrepository.FirebasePostRepository
import com.mcoolapp.menuhub.repository.postrepository.PostRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.ArrayList


class FirebaseUserRepository {

    var context: Context? = null
    private val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

    companion object {
        private const val USER_COLLECTION = "Users"
        private const val USER_CONFIDENTIAL_INFO = "confidencial_user_info"
        private const val ORGANIZATION_DATA = "organization_data"
     }


    private val remoteDB = FirebaseFirestore.getInstance().apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }


    @Ignore
    @Suppress("UNCHECKED_CAST")
    fun getUsersObserevedUsersList(userID: String): Observable<List<String>> {
        return Observable.create { emitter ->
            remoteDB.collection(USER_COLLECTION)
                .whereEqualTo("id", userID)
                .get()
                .addOnSuccessListener {
                    val obsUsersIDList = it.documents.get(0)["observableIDList"] as List<String>
                    emitter.onNext(obsUsersIDList)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
        }
    }

    fun createConfidentialInfo(userConfidentialInfo: UserConfidentialInfo): Observable<String> {
        return Observable.create { emitter ->
            remoteDB.collection(USER_CONFIDENTIAL_INFO)
                .add(userConfidentialInfo.toHashMapForCreate())
                .addOnSuccessListener {
                    val id = it.id
                    emitter.onNext(id)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
        }
    }

    fun getConfidentialInfo(id: String) : Observable<UserConfidentialInfo> {
        return  Observable.create { emitter ->
            remoteDB.collection(USER_CONFIDENTIAL_INFO)
                .document(id)
                .get()
                .addOnSuccessListener {
                    val confidentialInfo = UserConfidentialInfo(it.data!!)
                    emitter.onNext(confidentialInfo)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
        }
    }

    fun updateConfidentialInfo(id: String, userConfidentialInfo: UserConfidentialInfo): Observable<Boolean> {
        return Observable.create {emitter ->
            remoteDB.collection((USER_CONFIDENTIAL_INFO))
                .document(id)
                .update(userConfidentialInfo.toHashMapForUpdate())
                .addOnSuccessListener {
                    emitter.onNext(true)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {

                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
        }
    }

    fun createOrganizationData(organizationData: OrganizationData): Observable<String> {
        return Observable.create { emitter ->
            remoteDB.collection(ORGANIZATION_DATA)
                .add(organizationData.toHashMapForCreate())
                .addOnSuccessListener {
                    val id = it.id
                    emitter.onNext(id)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
        }
    }

    fun getOrganizationData(id: String) : Observable<OrganizationData> {
        return  Observable.create { emitter ->
            remoteDB.collection(ORGANIZATION_DATA)
                .document(id)
                .get()
                .addOnSuccessListener {
                    val organizationData = OrganizationData(it.data!!)
                    emitter.onNext(organizationData)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
        }
    }

    fun updateOrganizationData(id: String, organizationData: OrganizationData): Observable<Boolean> {
        return Observable.create {emitter ->
            remoteDB.collection((ORGANIZATION_DATA))
                .document(id)
                .update(organizationData.toHashMapForUpdate())
                .addOnSuccessListener {
                    emitter.onNext(true)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {

                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
        }
    }


    fun addPostID(postID: String): Observable<Boolean> {
        return Observable.create { emitter ->
            remoteDB.collection(USER_COLLECTION)
                .whereEqualTo("id", currentUserID)
                .get()
                .addOnCompleteListener {
                    if (!it.result!!.isEmpty) {
                        val hm = it.getResult()!!.documents.get(0).data
                        val user = User(hm!!)
                        (user.userPostIDList as ArrayList<String>).add(postID)

                        updateUser(user)
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                emitter.onNext(it!!)
                                if (!emitter.isDisposed) emitter.onComplete()
                            }, {
                                emitter.onError(it)
                                if (!emitter.isDisposed) emitter.onComplete()
                            })
                            .addTo(disposable)

                    }
                }
                .addOnFailureListener {
                    println("ERROR IN FIREBASEUSERREPOSITIRY addPostID")
                }
        }
    }

    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }


    fun getUserList(idList: List<String>): Observable<List<User>?> {
        return Observable.create() { emitter ->
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
                    System.out.println("FUR on error: " + it.toString())
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
                    if (it.result!!.documents.size > 0)
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

    fun getUserName(userID: String): Observable<String> {
        return Observable.create { emitter ->
            remoteDB.collection(USER_COLLECTION)
                .whereEqualTo("id", userID)
                .get()
                .addOnCompleteListener {
                    if (it.result!!.documents.size > 0)
                        emitter.onNext(it.result!!.documents.get(0).get("userName").toString())
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it.fillInStackTrace())
                    if (!emitter.isDisposed) emitter.onComplete()
                    println("Error in FirebaseUserrepository getUserName -> " + it.message)
                }
        }
    }

    fun isUserExistsInFBDB(id: String): Observable<Boolean> {
        return Observable.create { emitter ->
            remoteDB.collection(USER_COLLECTION)
                .whereEqualTo("id", id)
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        emitter.onNext(false)
                    } else {
                        emitter.onNext(true)
                    }
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