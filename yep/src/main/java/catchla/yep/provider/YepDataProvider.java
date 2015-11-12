package catchla.yep.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import catchla.yep.util.Utils;
import catchla.yep.util.YepSQLiteOpenHelper;

/**
 * Created by mariotaku on 15/7/9.
 */
public class YepDataProvider extends ContentProvider {

    private SQLiteDatabase mDatabase;

    @Override
    public boolean onCreate() {
        mDatabase = new YepSQLiteOpenHelper(getContext()).getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(@NonNull final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        final String table = Utils.getTableName(uri);
        if (table == null) return null;
        final Cursor cursor = mDatabase.query(table, projection, selection, selectionArgs, null, null, sortOrder);
        if (cursor != null) {
            setNotificationUri(cursor, uri);
        }
        return cursor;
    }

    @Override
    public String getType(@NonNull final Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull final Uri uri, @NonNull final ContentValues[] values) {
        final String table = Utils.getTableName(uri);
        if (table == null) return 0;
        int count = 0;
        mDatabase.beginTransaction();
        for (ContentValues valuesItem : values) {
            mDatabase.insert(table, null, valuesItem);
        }
        onDatabaseUpdated(uri);
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
        return count;
    }


    @Override
    public Uri insert(@NonNull final Uri uri, final ContentValues values) {
        final String table = Utils.getTableName(uri);
        if (table == null) return null;
        final long _id = mDatabase.insert(table, null, values);
        onDatabaseUpdated(uri);
        return Uri.withAppendedPath(uri, String.valueOf(_id));
    }

    @Override
    public int delete(@NonNull final Uri uri, final String selection, final String[] selectionArgs) {
        final String table = Utils.getTableName(uri);
        if (table == null) return 0;
        final int delete = mDatabase.delete(table, selection, selectionArgs);
        if (delete > 0)
            onDatabaseUpdated(uri);
        return delete;
    }

    @Override
    public int update(@NonNull final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
        final String table = Utils.getTableName(uri);
        if (table == null) return 0;
        final int update = mDatabase.update(table, values, selection, selectionArgs);
        if (update > 0)
            onDatabaseUpdated(uri);
        return update;
    }

    private void setNotificationUri(final Cursor cursor, final Uri uri) {
        final Context context = getContext();
        assert context != null;
        cursor.setNotificationUri(context.getContentResolver(), uri);
    }

    private void onDatabaseUpdated(final Uri uri) {
        final Context context = getContext();
        assert context != null;
        context.getContentResolver().notifyChange(uri, null);
    }
}
