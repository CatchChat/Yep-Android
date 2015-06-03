package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import io.realm.RealmObject;

/**
 * Created by mariotaku on 15/6/2.
 */
@JsonObject
public class Provider extends RealmObject {

    @JsonField(name = "name")
    private String name;
    @JsonField(name = "supported")
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
