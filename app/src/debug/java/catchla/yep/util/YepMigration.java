package catchla.yep.util;

import catchla.yep.model.Provider;
import catchla.yep.model.User;
import io.realm.Realm;
import io.realm.RealmMigration;
import io.realm.internal.ColumnType;
import io.realm.internal.Table;

/**
 * Created by mariotaku on 15/5/29.
 */
public class YepMigration implements RealmMigration {
    @Override
    public long execute(final Realm realm, long version) {
        if (version == 0) {
            Table providerTable = realm.getTable(Provider.class);
            realm.getTable(User.class).addColumnLink(ColumnType.LINK_LIST, "providers", providerTable);
            version = 1;
        }
        return version;
    }
}
