package dominando.android.hotel.repository.room

import androidx.lifecycle.LiveData
import androidx.room.*
import dominando.android.hotel.model.Hotel
import dominando.android.hotel.repository.http.Status
import dominando.android.hotel.repository.sqlite.*

@Dao
interface HotelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(hotel: Hotel): Long

    @Update
    fun update(hotel: Hotel): Int

    @Delete
    fun delete(vararg hotels: Hotel): Int

    @Query("SELECT * FROM $TABLE_HOTEL WHERE $COLUMN_ID = :id")
    fun hotelById(id: Long): LiveData<Hotel>

    @Query("""SELECT * FROM $TABLE_HOTEL
            WHERE $COLUMN_STATUS != ${Status.DELETE}
            AND $COLUMN_NAME LIKE :query
            ORDER BY $COLUMN_NAME""")
    fun search(query : String): LiveData<List<Hotel>>

    @Query("""SELECT * FROM $TABLE_HOTEL
            WHERE $COLUMN_SERVER_ID = :serverId""")
    fun hotelByServerId(serverId : Long): Hotel?

    @Query("""SELECT * FROM $TABLE_HOTEL
            WHERE $COLUMN_STATUS != ${Status.OK}""")
    fun pending(): List<Hotel>
}
