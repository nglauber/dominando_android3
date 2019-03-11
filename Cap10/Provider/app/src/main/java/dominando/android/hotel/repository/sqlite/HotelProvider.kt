package dominando.android.hotel.repository.sqlite

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri

class HotelProvider : ContentProvider() {

    private lateinit var helper: HotelSqlHelper

    override fun onCreate(): Boolean {
        helper = HotelSqlHelper(context)
        return true
    }
    override fun getType(uri: Uri): String? {
        val uriType = sUriMatcher.match(uri)
        return when (uriType) {
            TYPE_HOTEL_DIR ->
                return "${ContentResolver.CURSOR_DIR_BASE_TYPE}/dominando.android.hotel"
            TYPE_HOTEL_ITEM ->
                return "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/dominando.android.hotel"
            else -> null
        }
    }
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType = sUriMatcher.match(uri)
        val sqlDB = helper.writableDatabase
        val id: Long
        when (uriType) {
            TYPE_HOTEL_DIR ->
                id = sqlDB.insertWithOnConflict(
                    TABLE_HOTEL,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE)
            else ->
                throw IllegalArgumentException("URI não suportada: $uri")
        }
        context.contentResolver.notifyChange(uri, null)
        return Uri.withAppendedPath(CONTENT_URI, id.toString())
    }
    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        val uriType = sUriMatcher.match(uri)
        val sqlDB = helper.writableDatabase
        val rowsAffected = when (uriType) {
            TYPE_HOTEL_DIR ->
                sqlDB.update(
                    TABLE_HOTEL,
                    values,
                    selection,
                    selectionArgs)
            TYPE_HOTEL_ITEM -> {
                val id = uri.lastPathSegment
                sqlDB.update(
                    TABLE_HOTEL,
                    values,
                    "$COLUMN_ID= ?",
                    arrayOf(id))
            }
            else -> throw IllegalArgumentException(
                "URI não suportada: $uri")
        }
        context.contentResolver.notifyChange(uri, null)
        return rowsAffected
    }
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val uriType = sUriMatcher.match(uri)
        val sqlDB = helper.writableDatabase
        val rowsDeleted = when (uriType) {
            TYPE_HOTEL_DIR ->
                sqlDB.delete(
                    TABLE_HOTEL, selection, selectionArgs)
            TYPE_HOTEL_ITEM -> {
                val id = uri.lastPathSegment
                sqlDB.delete(
                    TABLE_HOTEL, "$COLUMN_ID = ?", arrayOf(id))
            }
            else -> throw IllegalArgumentException(
                "URI não suportada: $uri")
        }
        context.contentResolver.notifyChange(uri, null)
        return rowsDeleted
    }
    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val uriType = sUriMatcher.match(uri)
        val db = helper.writableDatabase
        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = TABLE_HOTEL
        val cursor: Cursor
        when (uriType) {
            TYPE_HOTEL_DIR ->
                cursor = queryBuilder.query(
                    db, projection, selection,
                    selectionArgs, null, null, sortOrder)
            TYPE_HOTEL_ITEM -> {
                queryBuilder.appendWhere("$COLUMN_ID = ?")
                cursor = queryBuilder.query(db, projection, selection,
                    arrayOf(uri.lastPathSegment),
                    null, null, null)
            }
            else -> throw IllegalArgumentException("URI não suportada: $uri")
        }
        cursor.setNotificationUri(context.contentResolver, uri)
        return cursor
    }

    companion object {
        private const val AUTHORITY = "dominando.android.hotel"
        private const val PATH = "hotels"
        private const val TYPE_HOTEL_DIR = 1
        private const val TYPE_HOTEL_ITEM = 2
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH")
        private val sUriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        init {
            sUriMatcher.addURI(AUTHORITY, PATH, TYPE_HOTEL_DIR)
            sUriMatcher.addURI(AUTHORITY, "$PATH/#", TYPE_HOTEL_ITEM)
        }
    }
}
