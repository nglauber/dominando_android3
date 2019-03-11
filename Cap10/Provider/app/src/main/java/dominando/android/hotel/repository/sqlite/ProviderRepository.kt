package dominando.android.hotel.repository.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import dominando.android.hotel.model.Hotel
import dominando.android.hotel.repository.HotelRepository

class ProviderRepository(val ctx: Context) : HotelRepository {

    override fun save(hotel: Hotel) {
        val uri = ctx.contentResolver.insert(
            HotelProvider.CONTENT_URI,
            getValues(hotel))
        val id = uri?.lastPathSegment?.toLong() ?: -1
        if (id != -1L) {
            hotel.id = id
        }
    }
    private fun getValues(hotel: Hotel): ContentValues {
        val cv = ContentValues()
        if (hotel.id > 0) {
            cv.put(COLUMN_ID, hotel.id)
        }
        cv.put(COLUMN_NAME, hotel.name)
        cv.put(COLUMN_ADDRESS, hotel.address)
        cv.put(COLUMN_RATING, hotel.rating)
        return cv
    }
    override fun remove(vararg hotels: Hotel) {
        hotels.forEach { hotel ->
            val uri = Uri.withAppendedPath(
                HotelProvider.CONTENT_URI, hotel.id.toString())
            ctx.contentResolver.delete(uri, null, null)
        }
    }
    override fun hotelById(id: Long, callback: (Hotel?) -> Unit) {
        val cursor = ctx.contentResolver.query(
            Uri.withAppendedPath(HotelProvider.CONTENT_URI, id.toString()),
            null, null, null, null)
        var hotel: Hotel? = null
        if (cursor?.moveToNext() == true) {
            hotel = hotelFromCursor(cursor)
        }
        cursor?.close()
        callback(hotel)
    }
    override fun search(term: String, callback: (List<Hotel>) -> Unit) {
        var where: String? = null
        var whereArgs: Array<String>? = null
        if (term.isNotEmpty()) {
            where = "$COLUMN_NAME LIKE ?"
            whereArgs = arrayOf("%$term%")
        }
        val cursor = ctx.contentResolver.query(
            HotelProvider.CONTENT_URI,
            null,
            where,
            whereArgs,
            COLUMN_NAME)
        val hotels = mutableListOf<Hotel>()
        while (cursor?.moveToNext() == true) {
            hotels.add(hotelFromCursor(cursor))
        }
        cursor?.close()
        callback(hotels)
    }
    private fun hotelFromCursor(cursor: Cursor): Hotel {
        val id = cursor.getLong(
            cursor.getColumnIndex(COLUMN_ID))
        val name = cursor.getString(
            cursor.getColumnIndex(COLUMN_NAME))
        val address = cursor.getString(
            cursor.getColumnIndex(COLUMN_ADDRESS))
        val rating = cursor.getFloat(
            cursor.getColumnIndex(COLUMN_RATING))
        return  Hotel(id, name, address, rating)
    }
}
