package dominando.android.hotel.repository.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import dominando.android.hotel.model.Hotel
import dominando.android.hotel.repository.HotelRepository
import java.util.ArrayList

class SQLiteRepository(ctx: Context): HotelRepository {
    private val helper: HotelSqlHelper = HotelSqlHelper(ctx)

    private fun insert(hotel: Hotel) {
        val db = helper.writableDatabase
        val cv = ContentValues().apply {
            put(COLUMN_NAME, hotel.name)
            put(COLUMN_ADDRESS, hotel.address)
            put(COLUMN_RATING, hotel.rating)
        }
        val id = db.insert(TABLE_HOTEL, null, cv)
        if (id != -1L) {
            hotel.id = id
        }
        db.close()
    }

    private fun update(hotel: Hotel) {
        val db = helper.writableDatabase
        val cv = ContentValues().apply {
            put(COLUMN_ID, hotel.id)
            put(COLUMN_NAME, hotel.name)
            put(COLUMN_ADDRESS, hotel.address)
            put(COLUMN_RATING, hotel.rating)
        }
        db.insertWithOnConflict(
            TABLE_HOTEL,
            null,
            cv,
            SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    override fun save(hotel: Hotel) {
        if (hotel.id == 0L) {
            insert(hotel)
        } else {
            update(hotel)
        }
    }

    override fun remove(vararg hotels: Hotel) {
        val db = helper.writableDatabase
        for (hotel in hotels) {
            db.delete(
                TABLE_HOTEL,
                "$COLUMN_ID = ?",
                arrayOf(hotel.id.toString()))
        }
        db.close()
    }

    override fun hotelById(id: Long, callback: (Hotel?)->Unit) {
        val sql = "SELECT * FROM $TABLE_HOTEL WHERE $COLUMN_ID = ?"
        val db = helper.readableDatabase
        val cursor = db.rawQuery(sql, arrayOf(id.toString()))
        val hotel = if (cursor.moveToNext()) hotelFromCursor(cursor) else null
        callback(hotel)
    }

    override fun search(term: String, callback: (List<Hotel>)->Unit) {
        var sql = "SELECT * FROM $TABLE_HOTEL"
        var args: Array<String>? = null
        if (term.isNotEmpty()) {
            sql += " WHERE $COLUMN_NAME LIKE ?"
            args = arrayOf("%$term%")
        }
        sql += " ORDER BY $COLUMN_NAME"
        val db = helper.readableDatabase
        val cursor = db.rawQuery(sql, args)
        val hotels = ArrayList<Hotel>()
        while (cursor.moveToNext()) {
            val hotel = hotelFromCursor(cursor)
            hotels.add(hotel)
        }
        cursor.close()
        db.close()
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
        return Hotel(id, name, address, rating)
    }
}
