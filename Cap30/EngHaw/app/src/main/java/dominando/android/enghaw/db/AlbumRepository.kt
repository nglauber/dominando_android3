package dominando.android.enghaw.db

import androidx.lifecycle.LiveData
import android.content.Context
import dominando.android.enghaw.model.Album

class AlbumRepository(context: Context) {
    private val db: AppDb by lazy {
        AppDb.getInstance(context) as AppDb
    }
    private val dao: AlbumDao by lazy {
        db.albumDao()
    }
    fun save(album: Album) {
        val id = dao.save(album)
        album.id = id
    }
    fun delete(album: Album) {
        dao.delete(album.title)
    }
    fun loadFavorites(): LiveData<List<Album>> {
        return dao.allAlbums()
    }
    fun isFavorite(album: Album): Boolean {
        return dao.albumByTitle(album.title).isNotEmpty()
    }
}