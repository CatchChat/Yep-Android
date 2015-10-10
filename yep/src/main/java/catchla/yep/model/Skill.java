package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

/**
 * Created by mariotaku on 15/5/12.
 */
@ParcelablePlease
@JsonObject
public class Skill implements Parcelable {

    public static final Creator<Skill> CREATOR = new Creator<Skill>() {
        @Override
        public Skill createFromParcel(Parcel in) {
            return new Skill(in);
        }

        @Override
        public Skill[] newArray(int size) {
            return new Skill[size];
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
    @JsonField(name = "cover_url")
    String coverUrl;
    @ParcelableThisPlease
    @JsonField(name = "category")
    SkillCategory category;

    public Skill() {

    }

    public Skill(final Parcel src) {
        SkillParcelablePlease.readFromParcel(this, src);
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        SkillParcelablePlease.writeToParcel(this, dest, flags);
    }
}
