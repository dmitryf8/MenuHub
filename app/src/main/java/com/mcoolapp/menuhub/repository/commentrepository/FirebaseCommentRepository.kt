package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.repository.commentrepository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.mcoolapp.menuhub.model.comments.Comment
import com.mcoolapp.menuhub.model.post.Post
import com.mcoolapp.menuhub.repository.postrepository.FirebasePostRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class FirebaseCommentRepository() {
    var context: Context? = null

    companion object {
        const val COMMENTS_COLLECTION = "comments"
    }

    private val remoteDB = FirebaseFirestore.getInstance().apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }



    fun getComment(id: String): Observable<Comment?> {
        return Observable.create() { emitter ->
            System.out.println("getComment(id) id = " + id)
            remoteDB.collection(COMMENTS_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener {
                    val doc = it
                    println(doc.data)
                    val comment = Comment(doc.data!!)
                    emitter.onNext(comment)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    System.out.println("comment saving error: " + it.toString())
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
        }
    }

    fun getCommentList(idList: List<String>): Observable<List<Comment>> {
        return Observable.create() { emitter ->
            remoteDB.collection(COMMENTS_COLLECTION)
                .whereIn("id", idList)
                .get()
                .addOnSuccessListener {
                    println("-------------------fbcr getCommentList ")
                    val list = arrayListOf<Comment>()
                    val docs = it.documents
                    for (doc in docs) {
                        list.add(Comment(doc.data!!))
                    }
                    emitter.onNext(list)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                    println("error in FireBaseCommentRepository.getCommentList(" + idList + ") -> " + it.message)
                }

        }
    }

    fun createComment(comment: Comment): Observable<String?> {
        return Observable.create { emitter ->
            val hm = comment.toHashMapForCreate()
            println("createComment() in FirebaseCommentRepository, hm -> " + hm)
            remoteDB.collection(COMMENTS_COLLECTION)
                .add(hm)
                .addOnSuccessListener {
                    emitter.onNext(it.id)

                    if (!emitter.isDisposed)
                        emitter.onComplete()
                }

                .addOnFailureListener {
                    emitter.onError(it)
                    println("*** " + it.message.toString())
                    if (!emitter.isDisposed)
                        emitter.onComplete()
                }

        }
    }

    fun updateComment(comment: Comment): Observable<Boolean?> {
        return Observable.create { emitter ->

            remoteDB.collection(COMMENTS_COLLECTION)
                .document(comment.id)
                .update(comment.toHashMapForUpdate())
                .addOnCompleteListener {
                    if (!emitter.isDisposed) emitter.onNext(true)
                    System.out.println(" SUCCESS override fun updateComment")

                }
                .addOnFailureListener {
                    emitter.onNext(false)
                    if (!emitter.isDisposed) emitter.onError(it.fillInStackTrace())
                    System.out.println(" Error override fun updateComment" + it)
                }

        }
    }
}