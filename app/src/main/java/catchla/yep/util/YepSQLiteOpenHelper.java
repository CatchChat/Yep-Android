package catchla.yep.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.mariotaku.sqliteqb.library.NewColumn;
import org.mariotaku.sqliteqb.library.SQLQueryBuilder;

import catchla.yep.Constants;
import catchla.yep.provider.YepDataStore.Friendships;

/**
 * Created by mariotaku on 15/8/5.
 */
public class YepSQLiteOpenHelper extends SQLiteOpenHelper implements Constants {
    public YepSQLiteOpenHelper(final Context context) {
        super(context, Constants.YEP_DATABASE_NAME, null, Constants.YEP_DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(createTable(Friendships.TABLE_NAME, Friendships.COLUMNS, Friendships.TYPES, true));
    }

    private String createTable(final String name, final String[] columns, final String[] types, final boolean createIfNotExists) {
        return SQLQueryBuilder.createTable(createIfNotExists, name).columns(NewColumn.createNewColumns(columns, types)).buildSQL();
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.beginTransaction();
        db.execSQL(SQLQueryBuilder.dropTable(true, Friendships.TABLE_NAME).getSQL());
        db.execSQL(createTable(Friendships.TABLE_NAME, Friendships.COLUMNS, Friendships.TYPES, true));
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
