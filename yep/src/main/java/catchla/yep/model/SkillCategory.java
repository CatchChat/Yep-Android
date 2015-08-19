package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import catchla.yep.model.util.SkillListTypeConverter;

/**
 * Created by mariotaku on 15/5/29.
 */
@JsonObject
public class SkillCategory {

    @JsonField(name = "id")
    private String id;
    @JsonField(name = "name")
    private String name;
    @JsonField(name = "name_string")
    private String nameString;
    @JsonField(name = "skills", typeConverter = SkillListTypeConverter.class)
    private List<Skill> skills;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getNameString() {
        return nameString;
    }

    public void setNameString(final String nameString) {
        this.nameString = nameString;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(final List<Skill> skills) {
        this.skills = skills;
    }
}
