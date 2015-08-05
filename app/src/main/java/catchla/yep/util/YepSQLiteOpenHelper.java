package catchla.yep.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import catchla.yep.Constants;

/**
 * Created by mariotaku on 15/8/5.
 */
public class YepSQLiteOpenHelper extends SQLiteOpenHelper implements Constants {
    public YepSQLiteOpenHelper(final Context context) {
        super(context, Constants.YEP_DATABASE_NAME, null, Constants.YEP_DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {

    }
}
