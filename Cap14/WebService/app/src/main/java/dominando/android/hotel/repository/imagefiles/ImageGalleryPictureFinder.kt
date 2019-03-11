package dominando.android.hotel.repository.imagefiles

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dominando.android.hotel.model.Hotel
import okhttp3.MediaType
import java.io.File
import java.io.FileOutputStream

class ImageGalleryPictureFinder(
    private val uploadsDir: File,
    private val resolver: ContentResolver) : FindHotelPicture {

    override fun pictureFile(hotel: Hotel): PictureToUpload {
        val file = File(uploadsDir, "${hotel.id}.jpg")
        if (file.exists() && !file.delete())
            throw IllegalArgumentException("Cannot find picture for this hotel")
        else {
            val filePath = Uri.parse(hotel.photoUrl)
            saveImageFromUri(filePath, file)
            return PictureToUpload(
                file,
                MediaType.parse(resolver.getType(filePath))!!
            )
        }
    }
    private fun saveImageFromUri(origin: Uri, destination: File) {
        try {
            if (!destination.exists()) {
                destination.createNewFile()
            }
            val input = resolver.openInputStream(origin)
            val options = BitmapFactory.Options()
            options.inSampleSize = 4
            val bmp = BitmapFactory.decodeStream(input, null, options)
            val bytes = FileOutputStream(destination)
            bmp?.compress(Bitmap.CompressFormat.JPEG, 80, bytes)
        } catch (e: Exception) {
            throw IllegalStateException("Cannot process image file")
        }
    }
}
