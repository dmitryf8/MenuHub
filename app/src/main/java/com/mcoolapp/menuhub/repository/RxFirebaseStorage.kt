package com.mcoolapp.menuhub.repository

import android.net.Uri
import com.google.common.primitives.Bytes
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.reactivex.Observable
import io.reactivex.Single

object  RxFirebaseStorage {

    fun saveImage(storageRef: StorageReference, uri: Uri): Observable<UploadTask.TaskSnapshot> {
        return Observable.create{emitter ->
            storageRef
                .putFile(uri)
                .addOnSuccessListener {
                    emitter.onNext(it)
                    System.out.println("RXFIREBASESTORAGE bytesTransferred = " + it.bytesTransferred + " byte(s) ---> name = " + it.metadata!!.name)
                    if (!emitter.isDisposed)
                        emitter.onComplete()
                }
                .addOnFailureListener{
                    if (!emitter.isDisposed) emitter.onError(it)
                }
        }
    }


    fun getURL(storageRef: StorageReference):Observable<Uri?> {

        System.out.println("- - - - - -- - - - -- - - - - -- - "+storageRef.path)
        return Observable.create { emitter ->
            storageRef.downloadUrl
                .addOnSuccessListener {
                    System.out.println("RxFirebaseStorage fun getURL onSuccess = " + it)
                    emitter.onNext(it!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                    System.out.println("RXFIREBASESTORAGE getURL error " + it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
        }
    }

}

