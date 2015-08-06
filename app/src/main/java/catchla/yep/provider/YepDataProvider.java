package catchla.yep.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
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
    public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        final String table = Utils.getTableName(uri);
        if (table == null) return null;
        return mDatabase.query(table, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(final Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(final Uri uri, @NonNull final ContentValues[] values) {
        final String table = Utils.getTableName(uri);
        if (table == null) return 0;
        int count = 0;
        mDatabase.beginTransaction();
        for (ContentValues valuesItem : values) {
            mDatabase.insert(table, null, valuesItem);
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
        return count;
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        final String table = Utils.getTableName(uri);
        if (table == null) return null;
        final long _id = mDatabase.insert(table, null, values);
        return Uri.withAppendedPath(uri, String.valueOf(_id));
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        final String table = Utils.getTableName(uri);
        if (table == null) return 0;
        return mDatabase.delete(table, selection, selectionArgs);
    }

    @Override
    public int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
        final String table = Utils.getTableName(uri);
        if (table == null) return 0;
        return mDatabase.update(table, values, selection, selectionArgs);
    }
}
