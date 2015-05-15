package catchla.yep.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by mariotaku on 15/5/12.
 */
public class Skill extends RealmObject {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("name_string")
    private String nameString;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameString() {
        return nameString;
    }

    public void setNameString(String nameString) {
        this.nameString = nameString;
    }
}
