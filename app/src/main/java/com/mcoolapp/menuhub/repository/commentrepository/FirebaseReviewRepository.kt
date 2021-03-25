package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.repository.commentrepository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.mcoolapp.menuhub.model.comments.Comment
import com.mcoolapp.menuhub.model.comments.Review
import com.mcoolapp.menuhub.repository.postrepository.FirebasePostRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class FirebaseReviewRepository {
    var context: Context? = null

    companion object{
        const val REVIEWS_COLLECTION = "reviews"
    }

    private val remoteDB = FirebaseFirestore.getInstance().apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    fun getReview(id: String): Observable<Review?> {
        return Observable.create() { emitter ->
            System.out.println("getReview(id) id = " + id)
            remoteDB.collection(REVIEWS_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener {
                    val doc = it
                    println(doc.data)
                    val review = Review(doc.data!!)

                    emitter.onNext(review)

                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    System.out.println("review saving error: " + it.toString())
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
        }
    }

    fun getReviewList(idList: List<String>): Observable<List<Review>> {
        return Observable.create() { emitter ->
            remoteDB.collection(REVIEWS_COLLECTION)
                .whereIn("id", idList)
                .get()
                .addOnSuccessListener {
                    println("-------------------fbrr getReviewList ")
                    val list = arrayListOf<Review>()
                    val docs = it.documents
                    for (doc in docs) {
                        list.add(Review(doc.data!!))
                    }
                    emitter.onNext(list)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                    println("error in FireBaseReviewRepository.getCommentList(" + idList + ") -> " + it.message)
                }

        }
    }

    fun createReview(review: Review): Observable<String?> {
        return Observable.create { emitter ->
            val hm = review.toHashMapForCreate()
            println("createReview() in FirebaseReviewRepository, hm -> " + hm)
            remoteDB.collection(REVIEWS_COLLECTION)
                .add(hm)
                .addOnSuccessListener {
                    review.id = it.id
                    updateReview(review)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            System.out.println("review created with id = " + review.id)
                        },
                            {
                                emitter.onError(it)
                                System.out.println("review creating error with id = " + review.id)
                                if (!emitter.isDisposed)
                                    emitter.onComplete()
                            })

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

    fun updateReview(review: Review): Observable<Boolean?> {
        return Observable.create { emitter ->

            remoteDB.collection(FirebasePostRepository.POST_COLLECTION)
                .document(review.id)
                .update(review.toHashMapForUpdate())
                .addOnCompleteListener {
                    if (!emitter.isDisposed) emitter.onNext(true)
                    System.out.println(" SUCCESS override fun updateReview")

                }
                .addOnFailureListener {
                    emitter.onNext(false)
                    if (!emitter.isDisposed) emitter.onError(it.fillInStackTrace())
                    System.out.println(" Error override fun updateReview" + it)
                }

        }
    }
}