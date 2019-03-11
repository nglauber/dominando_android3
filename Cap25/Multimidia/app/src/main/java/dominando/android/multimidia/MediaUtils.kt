package dominando.android.multimidia

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.format.DateFormat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.IOException
import java.util.*

object MediaUtils {
    const val REQUEST_CODE_PHOTO = 1
    const val REQUEST_CODE_VIDEO = 2
    const val REQUEST_CODE_AUDIO = 3
    const val PROVIDER_AUTHORITY = "dominando.android.multimidia.fileprovider"

    private const val PREFERENCE_MEDIA = "midia_prefs"
    private const val MEDIA_FOLDER = "Dominando_Android"

    enum class MediaType(val extension: String, val preferenceKey: String) {
        MEDIA_PHOTO(".jpg", "last_photo"),
        MEDIA_VIDEO(".mp4", "last_video"),
        MEDIA_AUDIO(".3gp", "last_audio")
    }

    fun newMedia(mediaType: MediaType): File {
        val fileName = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()
        val mediaDir = File(
            Environment.getExternalStorageDirectory(),
            MEDIA_FOLDER)
        if (!mediaDir.exists()) {
            if (!mediaDir.mkdirs()) {
                throw IllegalArgumentException("Fail to create directories")
            }
        }
        return File(mediaDir, fileName + mediaType.extension)
    }

    fun saveLastMediaPath(ctx: Context, mediaType: MediaType, mediaPath: String) {
        ctx.getSharedPreferences(PREFERENCE_MEDIA, Context.MODE_PRIVATE)
            .edit()
            .putString(mediaType.preferenceKey, mediaPath)
            .apply()
        ctx.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
            data = Uri.parse(mediaPath)
        })
    }

    fun getLastMediaPath(ctx: Context, mediaType: MediaType): String? {
        return ctx.getSharedPreferences(PREFERENCE_MEDIA, Context.MODE_PRIVATE)
            .getString(mediaType.preferenceKey, null)
    }

    fun loadImage(imageFile: File, width: Int, height: Int): Bitmap? {
        if (width == 0 || height == 0) return null
        val bmpOptions = BitmapFactory.Options()
        bmpOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFile.absolutePath, bmpOptions)
        val realPhotoWidth = bmpOptions.outWidth
        val realPhotoHeight = bmpOptions.outHeight
        val scale = Math.min(
            realPhotoWidth / width,
            realPhotoHeight / height)
        bmpOptions.inJustDecodeBounds = false
        bmpOptions.inSampleSize = scale
        bmpOptions.inPreferredConfig = Bitmap.Config.RGB_565
        var bitmap = BitmapFactory.decodeFile(
            imageFile.absolutePath, bmpOptions)
        bitmap = rotateImage(bitmap, imageFile.absolutePath)
        return bitmap
    }

    private fun rotateImage(bitmap: Bitmap, filePath: String): Bitmap {
        var bmp = bitmap
        try {
            val ei = ExifInterface(filePath)
            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> bmp = rotateImage(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> bmp = rotateImage(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> bmp = rotateImage(bitmap, 270f)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmp
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0,
            source.width, source.height,
            matrix, true)
    }

    fun getVideoUri(context: Context): Uri {
        val newVideoFile = MediaUtils.newMedia(MediaUtils.MediaType.MEDIA_VIDEO)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context,
                MediaUtils.PROVIDER_AUTHORITY, newVideoFile)
        } else {
            Uri.fromFile(newVideoFile)
        }
    }
}

