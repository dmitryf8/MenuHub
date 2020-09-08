package com.mcoolapp.menuhub.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.google.firebase.storage.FirebaseStorage
import com.mcoolapp.menuhub.model.image.ImageWithBucket
import com.mcoolapp.menuhub.model.image.ImageWithId
import com.mcoolapp.menuhub.model.menu.Table
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.glxn.qrgen.android.QRCode
import net.glxn.qrgen.core.image.ImageType
import java.io.ByteArrayOutputStream


class ImageRepository(val context: Context) {
    private var dataBase = DataBase.INSTANCE

    fun saveImage(uri: Uri, buckatId: String): Observable<String?> {
        return Observable.create { emitter ->
            val storageRef = FirebaseStorage.getInstance()
            val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            val randomString = (1..32)
                .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("");
            val ref = storageRef.reference.child(buckatId).child(randomString)


            val bitmap = MediaStore.Images.Media.getBitmap(
                context.contentResolver, uri
            )
            var btmp = bitmap
            try {
                if (bitmap.width * bitmap.height > 3 * 100 * 1024) {
                    println("bitmap width = " + bitmap.width)
                    println("bitmap height = " + bitmap.height)

                    println("bitmap.width * bitmap.height > 3 * 100 * 1024")
                    val size = bitmap.width * bitmap.height
                    println("bitmap size = " + size)
                    val coef = size / (3 * 100 * 1024)
                    println("coef = " + coef)
                    val width = bitmap.width / Math.sqrt(coef.toDouble())
                    println("width = " + width)
                    val height = bitmap.height / Math.sqrt(coef.toDouble())
                    println("height = " + height)
                    btmp = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), false)
                }
            } catch (e: Exception) {
                println("Error in " + e.message)
            }


            val bos = ByteArrayOutputStream()
            btmp.compress(CompressFormat.PNG, 0, bos)
            val bitmapdata: ByteArray = bos.toByteArray()



            System.out.println("randomString =" + randomString)
            ref.putBytes(bitmapdata)
                .addOnSuccessListener {
                    val id = it.metadata!!.name!!

                    System.out.println("ImageRepository -> saveImage -> putStreeam -> imageId = " + id)
                    emitter.onNext(id)

                    val imageWithId = ImageWithId()
                    imageWithId.id = id
                    imageWithId.data = bitmapdata
                    println("save image  put Stream imageWithId  data.size ->" + imageWithId.data.size)
                    isExistsInRoom(id)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            if (it!!) {
                                updateImageInRoom(imageWithId)
                            } else {
                                saveImageInRoom(imageWithId)
                            }
                        }, {
                            System.out.println("isExists(imageWithId) error " + it.stackTrace!!.contentToString())
                        }
                        )
                        .addTo(disposable)

                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
        }
        /**RxFirebaseStorage.saveImage(storageRef, uri)
        .subscribeOn(Schedulers.io())
        .subscribe({
        val id = it.metadata!!.name!!
        System.out.println("it.metadata!!.name!! = " + id)
        emitter.onNext(id)

        val data = context.contentResolver.openInputStream(uri)?.readBytes()
        val imageWithId = ImageWithId()
        imageWithId.id = id
        imageWithId.data = data!!
        isExistsInRoom(id)
        .subscribeOn(Schedulers.io())
        .subscribe({
        if (it!!) {updateImageInRoom(imageWithId)} else {saveImageInRoom(imageWithId)}
        } ,{
        System.out.println("isExists(imageWithId) " + it)
        }
        )
        .addTo(disposable)

        if (!emitter.isDisposed) emitter.onComplete()
        }, {
        emitter.onError(it)
        if (!emitter.isDisposed) emitter.onComplete()
        })

         **/

    }


    fun getImageFromBucket(imageWithBucket: ImageWithBucket): Observable<Bitmap?> {
        return Observable.create { emitter ->
            dataBase = DataBase.getAppDataBase(context = context)
            val imageWithId = dataBase!!.imageWithIdDao().getImage(imageWithBucket.imageId!!)

            if (imageWithId == null || imageWithId!!.data == null ) {
                val storageRef =
                    FirebaseStorage.getInstance("gs://menuhub-3474a.appspot.com")
                        .getReference(imageWithBucket.bucketName)
                val imageRef = storageRef.child(imageWithBucket.imageId!!)


                val TEN_MEGABYTE: Long = 10 * 1024 * 1024
                imageRef.getBytes(TEN_MEGABYTE)
                    .addOnSuccessListener {
                        System.out.println("byteArray size = " + it!!.size)
                        emitter.onNext(BitmapFactory.decodeByteArray(it, 0, it.size))
                        val imaWithId = ImageWithId()
                        imaWithId.id = imageWithBucket.imageId!!
                        imaWithId.data = it

                        saveImageInRoom(imaWithId)

                        if (!emitter.isDisposed) emitter.onComplete()
                    }
                    .addOnFailureListener {
                        System.out.println("error in ImageRepository-> getUrl -> readBytes()= " + it)
                        emitter.onError(it)
                        if (!emitter.isDisposed) emitter.onComplete()
                    }

            } else {
                getBitmap(imageWithId)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        emitter.onNext(it!!)
                        if (emitter.isDisposed) emitter.onComplete()
                    }, {
                        emitter.onError(it!!)
                        if (emitter.isDisposed) emitter.onComplete()
                    })
                    .addTo(disposable)
                if (emitter.isDisposed) emitter.onComplete()

            }
        }
    }


    private val disposable = CompositeDisposable()

    fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    private fun getBitmap(imageWithId: ImageWithId): Observable<Bitmap?> {
        return Observable.create { emitter ->
            var bitmap = BitmapFactory.decodeByteArray(
                imageWithId.data,
                0,
                imageWithId.data.size
            )

            emitter.onNext(bitmap)
            if (emitter.isDisposed) emitter.onComplete()
        }
    }

    fun generateQRCode(table: Table): Observable<String> {
        return Observable.create() {emitter ->
            val tableID = table.id
            println("table")
            val byteArrayOutputStream = QRCode.from("com.mcoolapp.menuhub " + tableID).to(ImageType.JPG).stream()
            val bytes = byteArrayOutputStream.toByteArray()
            val storageRef = FirebaseStorage.getInstance()
            val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            val randomString = (1..32)
                .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("");
            val ref = storageRef.reference.child("bucket" + table.ownerId).child(randomString)

            ref.putBytes(bytes)
                .addOnSuccessListener {
                    emitter.onNext(it.metadata!!.name!!)
                    if (!emitter.isDisposed) emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                    if (!emitter.isDisposed) emitter.onComplete()
                    println("error savin qrcode -> " + it.message)
                }

        }
    }

    private fun isExistsInRoom(id: String): Observable<Boolean?> {
        System.out.println("private fun isExists(imageWithId: ImageWithId): Observable<Boolean?>")

        return Observable.create { emitter ->
            dataBase = DataBase.getAppDataBase(context)
            val isExists = dataBase!!.imageWithIdDao().isExist(id)
            System.out.println("isExists == " + isExists)
            emitter.onNext(isExists)
            if (!emitter.isDisposed) emitter.onComplete()
        }
    }


    private fun saveImageInRoom(imageWithId: ImageWithId) {

        val job: Job = GlobalScope.launch(Dispatchers.IO) {
            dataBase = DataBase.getAppDataBase(context)
            dataBase!!.imageWithIdDao().insert(imageWithId)
            }
        job.start()
    }

    private fun updateImageInRoom(imageWithId: ImageWithId) {
        val job: Job = GlobalScope.launch(Dispatchers.IO) {
            dataBase = DataBase.getAppDataBase(context)
            dataBase!!.imageWithIdDao().update(imageWithId)
            System.out.println("-------------------------Image updated in room---------------------------------------")
        }
        job.start()
    }
}