package catchla.yep.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

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
        return null;
    }

    @Override
    public String getType(final Uri uri) {
        return null;
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        return null;
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
        return 0;
    }
}
