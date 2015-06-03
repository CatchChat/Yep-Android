package catchla.yep.model;

import io.realm.RealmObject;

/**
 * Created by mariotaku on 15/6/2.
 */
public class Provider extends RealmObject {

    private String name;
    private boolean supported;

    public Provider() {
    }

    public Provider(final String name, final boolean supported) {
        this.name = name;
        this.supported = supported;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isSupported() {
        return supported;
    }

    public void setSupported(final boolean supported) {
        this.supported = supported;
    }

}
