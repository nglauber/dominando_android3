package dominando.android.enghaw.db

import android.content.Context
import androidx.room.*
import dominando.android.enghaw.model.Album

@Database(entities = [Album::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDb: RoomDatabase() {
    abstract fun albumDao(): AlbumDao

    companion object {
        private val DB_NAME = "engHawDb"
        private var INSTANCE: AppDb? = null

        fun getInstance(context: Context): AppDb? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDb::class.java,
                    DB_NAME)
                    .build()
            }
            return INSTANCE
        }
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}