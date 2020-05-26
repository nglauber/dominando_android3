package dominando.android.enghaw.db

import androidx.lifecycle.LiveData
import dominando.android.enghaw.model.Album
import androidx.room.*

@Dao
interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(album: Album): Long
    @Query("DELETE FROM Album WHERE title = :title")
    fun delete(title: String)
    @Query("SELECT * FROM Album ORDER BY year")
    fun allAlbums(): LiveData<List<Album>>
    @Query("SELECT * FROM Album WHERE title LIKE :title")
    fun albumByTitle(title: String): List<Album>
}