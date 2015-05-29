package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import io.realm.RealmObject;

/**
 * Created by mariotaku on 15/5/12.
 */
@JsonObject
public class Skill extends RealmObject {

    @JsonField(name = "id")
    private String id;
    @JsonField(name = "name")
    private String name;
    @JsonField(name = "name_string")
    private String nameString;
    @JsonField(name = "cover_url")
    private String coverUrl;
    @JsonField(name = "category")
    private SkillCategory category;

    public SkillCategory getCategory() {
        return category;
    }

    public void setCategory(final SkillCategory category) {
        this.category = category;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(final String coverUrl) {
        this.coverUrl = coverUrl;
    }

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
