package com.mcoolapp.menuhub.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.RectF
import android.net.Uri
import android.provider.MediaStore
import android.util.Log

import java.io.FileNotFoundException
import java.io.IOException
import java.io.InvalidObjectException

/**
 * Creates resized images without exploding memory. Uses the method described in android
 * documentation concerning bitmap allocation, which is to subsample the image to a smaller size,
 * close to some expected size. This is required because the android standard library is unable to
 * create a reduced size image from an image file using memory comparable to the final size (and
 * loading a full sized multi-megapixel picture for processing may exceed application memory budget).
 *
 * implementation by user @hdante
 * http://stackoverflow.com/users/1797000/hdante
 */

class UserPicture(internal var uri: Uri, internal var resolver: ContentResolver) {
    internal var path: String? = null
    var orientation: Matrix? = null
    internal var storedHeight: Int = 0
    internal var storedWidth: Int = 0

    private val information: Boolean
        @Throws(IOException::class)
        get() {
            if (informationFromMediaDatabase)
                return true

            return if (informationFromFileSystem) true else false

        }

    /* Support for gallery apps and remote ("picasa") images */
    private val informationFromMediaDatabase: Boolean
        get() {
            val fields =
                arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION)
            val cursor = resolver.query(uri, fields, null, null, null) ?: return false

            cursor.moveToFirst()
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            val orientation =
                cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION))
            this.orientation = Matrix()
            this.orientation?.setRotate(orientation.toFloat())
            cursor.close()

            return true
        }

    /* Support for file managers and dropbox */
    private/* Identity matrix */ val informationFromFileSystem: Boolean
        @Throws(IOException::class)
        get() {
            path = uri.path

            Log.d("mylog", "path = " + path)

            if (path == null)
                return false
            return true
        }

    private/* The input stream could be reset instead of closed and reopened if it were possible
           to reliably wrap the input stream on a buffered stream, but it's not possible because
           decodeStream() places an upper read limit of 1024 bytes for a reset to be made (it calls
           mark(1024) on the stream). */ val storedDimensions: Boolean
        @Throws(IOException::class)
        get() {
            val input = resolver.openInputStream(uri)
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(resolver.openInputStream(uri), null, options)
            input!!.close()

            if (options.outHeight <= 0 || options.outWidth <= 0)
                return false

            storedHeight = options.outHeight
            storedWidth = options.outWidth

            return true
        }

    val bitmap: Bitmap?
        @Throws(IOException::class)
        get() {
            if (!information)
                throw FileNotFoundException()

            if (!storedDimensions)
                throw InvalidObjectException(null)

            val rect = RectF(0f, 0f, storedWidth.toFloat(), storedHeight.toFloat())
            orientation?.mapRect(rect)
            var width = rect.width().toInt()
            var height = rect.height().toInt()
            var subSample = 1

            while (width > MAX_WIDTH || height > MAX_HEIGHT) {
                width /= 2
                height /= 2
                subSample *= 2
            }

            if (width == 0 || height == 0)
                throw InvalidObjectException(null)

            val options = BitmapFactory.Options()
            options.inSampleSize = subSample
            val subSampled =
                BitmapFactory.decodeStream(resolver.openInputStream(uri), null, options)

            val picture: Bitmap?
            if (orientation!!.isIdentity) {
                picture = Bitmap.createBitmap(
                    subSampled!!, 0, 0, options.outWidth, options.outHeight,
                    orientation, false
                )
                subSampled.recycle()
            } else
                picture = subSampled

            return picture
        }

    companion object {

        internal var MAX_WIDTH = 600
        internal var MAX_HEIGHT = 800
    }
}
