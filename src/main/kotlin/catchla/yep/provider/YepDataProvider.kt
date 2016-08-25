package catchla.yep.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import catchla.yep.Constants
import catchla.yep.util.Utils
import catchla.yep.util.YepSQLiteOpenHelper
import catchla.yep.util.content.createChatHistorySearchCursor

/**
 * Created by mariotaku on 15/7/9.
 */
class YepDataProvider : ContentProvider() {

    private lateinit var db: SQLiteDatabase

    override fun onCreate(): Boolean {
        db = YepSQLiteOpenHelper(context).writableDatabase
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val cursor: Cursor?
        when (Utils.getTableId(uri)) {
            Constants.VIRTUAL_TABLE_ID_CONVERSATIONS -> {
                val accountId = uri.pathSegments[uri.pathSegments.lastIndex - 1]
                val query = uri.pathSegments[uri.pathSegments.lastIndex]
                cursor = createChatHistorySearchCursor(db, accountId, query)
            }
            else -> {
                val table = Utils.getTableName(uri) ?: return null
                cursor = db.query(table, projection, selection, selectionArgs, null, null, sortOrder)
            }
        }
        cursor?.let {
            setNotificationUri(it, uri)
        }
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun bulkInsert(uri: Uri, values: Array<ContentValues>): Int {
        val table = Utils.getTableName(uri) ?: return 0
        val count = 0
        db.beginTransaction()
        for (valuesItem in values) {
            db.insert(table, null, valuesItem)
        }
        onDatabaseUpdated(uri)
        db.setTransactionSuccessful()
        db.endTransaction()
        return count
    }


    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val table = Utils.getTableName(uri) ?: return null
        val _id = db.insert(table, null, values)
        onDatabaseUpdated(uri)
        return Uri.withAppendedPath(uri, _id.toString())
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val table = Utils.getTableName(uri) ?: return 0
        val delete = db.delete(table, selection, selectionArgs)
        if (delete > 0)
            onDatabaseUpdated(uri)
        return delete
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val table = Utils.getTableName(uri) ?: return 0
        val update = db.update(table, values, selection, selectionArgs)
        if (update > 0)
            onDatabaseUpdated(uri)
        return update
    }

    private fun setNotificationUri(cursor: Cursor, uri: Uri) {
        val context = context!!
        cursor.setNotificationUri(context.contentResolver, uri)
    }

    private fun onDatabaseUpdated(uri: Uri) {
        val context = context!!
        context.contentResolver.notifyChange(uri, null)
    }
}
