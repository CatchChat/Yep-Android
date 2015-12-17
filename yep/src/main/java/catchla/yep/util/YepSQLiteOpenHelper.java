package catchla.yep.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.mariotaku.sqliteqb.library.Columns;
import org.mariotaku.sqliteqb.library.Constraint;
import org.mariotaku.sqliteqb.library.NewColumn;
import org.mariotaku.sqliteqb.library.OnConflict;
import org.mariotaku.sqliteqb.library.SQLQueryBuilder;

import catchla.yep.Constants;
import catchla.yep.provider.YepDataStore.Circles;
import catchla.yep.provider.YepDataStore.Conversations;
import catchla.yep.provider.YepDataStore.Friendships;
import catchla.yep.provider.YepDataStore.Messages;

/**
 * Created by mariotaku on 15/8/5.
 */
public class YepSQLiteOpenHelper extends SQLiteOpenHelper implements Constants {
    public YepSQLiteOpenHelper(final Context context) {
        super(context, YEP_DATABASE_NAME, null, YEP_DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.beginTransaction();
        db.execSQL(createTable(Friendships.TABLE_NAME, Friendships.COLUMNS, Friendships.TYPES, true));
        db.execSQL(createTable(Messages.TABLE_NAME, Messages.COLUMNS, Messages.TYPES, true));
        db.execSQL(createTable(Conversations.TABLE_NAME, Conversations.COLUMNS, Conversations.TYPES, true,
                Constraint.unique(new Columns(Conversations.CONVERSATION_ID), OnConflict.REPLACE)));
        db.execSQL(createTable(Circles.TABLE_NAME, Circles.COLUMNS, Circles.TYPES, true,
                Constraint.unique(new Columns(Circles.CIRCLE_ID), OnConflict.REPLACE)));
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private String createTable(final String name, final String[] columns, final String[] types, final boolean createIfNotExists, Constraint... constraints) {
        return SQLQueryBuilder.createTable(createIfNotExists, name)
                .columns(NewColumn.createNewColumns(columns, types))
                .constraint(constraints)
                .buildSQL();
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.beginTransaction();
        db.execSQL(SQLQueryBuilder.dropTable(true, Friendships.TABLE_NAME).getSQL());
        db.execSQL(createTable(Friendships.TABLE_NAME, Friendships.COLUMNS, Friendships.TYPES, true));
        db.execSQL(SQLQueryBuilder.dropTable(true, Messages.TABLE_NAME).getSQL());
        db.execSQL(createTable(Messages.TABLE_NAME, Messages.COLUMNS, Messages.TYPES, true));
        db.execSQL(SQLQueryBuilder.dropTable(true, Conversations.TABLE_NAME).getSQL());
        db.execSQL(createTable(Conversations.TABLE_NAME, Conversations.COLUMNS, Conversations.TYPES, true,
                Constraint.unique(new Columns(Conversations.CONVERSATION_ID), OnConflict.REPLACE)));
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
