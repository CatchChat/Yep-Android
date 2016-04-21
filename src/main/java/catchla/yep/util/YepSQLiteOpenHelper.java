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
import catchla.yep.model.CircleTableInfo;
import catchla.yep.model.ConversationTableInfo;
import catchla.yep.model.FriendshipTableInfo;
import catchla.yep.model.MessageTableInfo;
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
        createTables(db);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private void createTables(final SQLiteDatabase db) {
        db.execSQL(createTable(Friendships.TABLE_NAME, FriendshipTableInfo.COLUMNS, FriendshipTableInfo.TYPES, true,
                Constraint.unique(new Columns(Friendships.ACCOUNT_ID, Friendships.FRIENDSHIP_ID),
                        OnConflict.REPLACE)));
        db.execSQL(createTable(Messages.TABLE_NAME, MessageTableInfo.COLUMNS, MessageTableInfo.TYPES, true,
                Constraint.unique(new Columns(Messages.ACCOUNT_ID, Messages.MESSAGE_ID),
                        OnConflict.REPLACE)));
        db.execSQL(createTable(Conversations.TABLE_NAME, ConversationTableInfo.COLUMNS, ConversationTableInfo.TYPES, true,
                Constraint.unique(new Columns(Conversations.ACCOUNT_ID, Conversations.CONVERSATION_ID),
                        OnConflict.REPLACE)));
        db.execSQL(createTable(Circles.TABLE_NAME, CircleTableInfo.COLUMNS, CircleTableInfo.TYPES, true,
                Constraint.unique(new Columns(Circles.ACCOUNT_ID, Circles.CIRCLE_ID), OnConflict.REPLACE)));
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
        db.execSQL(SQLQueryBuilder.dropTable(true, Messages.TABLE_NAME).getSQL());
        db.execSQL(SQLQueryBuilder.dropTable(true, Conversations.TABLE_NAME).getSQL());
        db.execSQL(SQLQueryBuilder.dropTable(true, Circles.TABLE_NAME).getSQL());
        createTables(db);
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
