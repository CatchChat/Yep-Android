package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

import java.util.List;

import catchla.yep.model.util.SkillListTypeConverter;

/**
 * Created by mariotaku on 15/5/29.
 */
@JsonObject
@ParcelablePlease
public class SkillCategory implements Parcelable {

    public static final Creator<SkillCategory> CREATOR = new Creator<SkillCategory>() {
        @Override
        public SkillCategory createFromParcel(Parcel in) {
            return new SkillCategory(in);
        }

        @Override
        public SkillCategory[] newArray(int size) {
            return new SkillCategory[size];
        }
    };
    @ParcelableThisPlease
    @JsonField(name = "id")
    String id;
    @ParcelableThisPlease
    @JsonField(name = "name")
    String name;
    @ParcelableThisPlease
    @JsonField(name = "name_string")
    String nameString;
    @ParcelableThisPlease
    @JsonField(name = "skills", typeConverter = SkillListTypeConverter.class)
    List<Skill> skills;

    public SkillCategory(Parcel src) {
        SkillCategoryParcelablePlease.readFromParcel(this, src);
    }

    public SkillCategory() {
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        SkillCategoryParcelablePlease.writeToParcel(this, dest, flags);
    }
}
