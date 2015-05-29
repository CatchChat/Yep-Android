package catchla.yep.util;

import io.realm.Realm;
import io.realm.RealmMigration;

/**
 * Created by mariotaku on 15/5/29.
 */
public class YepMigration implements RealmMigration {
    @Override
    public long execute(final Realm realm, final long version) {
        return 0;
    }
}
