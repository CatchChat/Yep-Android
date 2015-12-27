package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

import java.util.List;

import catchla.yep.model.util.ProviderConverter;
import catchla.yep.model.util.SkillListTypeConverter;

/**
 * Created by mariotaku on 15/5/8.
 */
@ParcelablePlease
@JsonObject
public class User implements Parcelable {

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @ParcelableThisPlease
    @JsonField(name = "master_skills", typeConverter = SkillListTypeConverter.class)
    List<Skill> masterSkills;
    @ParcelableThisPlease
    @JsonField(name = "learning_skills", typeConverter = SkillListTypeConverter.class)
    List<Skill> learningSkills;
    @ParcelableThisPlease
    @JsonField(name = "id")
    String id;
    @ParcelableThisPlease
    @JsonField(name = "username")
    String username;
    @ParcelableThisPlease
    @JsonField(name = "nickname")
    String nickname;
    @ParcelableThisPlease
    @JsonField(name = "introduction")
    String introduction;
    @ParcelableThisPlease
    @JsonField(name = "avatar_url")
    String avatarUrl;
    @ParcelableThisPlease
    @JsonField(name = "avatar")
    Avatar avatar;
    @ParcelableThisPlease
    @JsonField(name = "mobile")
    String mobile;
    @ParcelableThisPlease
    @JsonField(name = "phone_code")
    String phoneCode;
    @ParcelableThisPlease
    @JsonField(name = "contact_name")
    String contactName;
    @ParcelableThisPlease
    @JsonField(name = "providers", typeConverter = ProviderConverter.class)
    List<Provider> providers;
    @ParcelableThisPlease
    @JsonField(name = "latitude")
    double latitude = Double.NaN;
    @ParcelableThisPlease
    @JsonField(name = "longitude")
    double longitude = Double.NaN;
    @ParcelableThisPlease
    @JsonField(name = "badge")
    String badge;
    @ParcelableThisPlease
    LatLng location;

    public User() {

    }

    public User(final Parcel src) {
        UserParcelablePlease.readFromParcel(this, src);
    }

    @Override
    public String toString() {
        return "User{" +
                "avatarUrl='" + avatarUrl + '\'' +
                ", masterSkills=" + masterSkills +
                ", learningSkills=" + learningSkills +
                ", id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", introduction='" + introduction + '\'' +
                ", mobile='" + mobile + '\'' +
                ", phoneCode='" + phoneCode + '\'' +
                ", contactName='" + contactName + '\'' +
                ", providers=" + providers +
                ", badge='" + badge + '\'' +
                ", location=" + location +
                '}';
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(final LatLng location) {
        this.location = location;
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public void setProviders(final List<Provider> providers) {
        this.providers = providers;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(final String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @Nullable
    public String getAvatarUrl() {
        if (avatar != null) return avatar.url;
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Nullable
    public Avatar getAvatar() {
        return avatar;
    }

    @Nullable
    public String getAvatarThumbUrl() {
        if (avatar != null) return avatar.thumbUrl;
        return avatarUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Skill> getMasterSkills() {
        return masterSkills;
    }

    public void setMasterSkills(List<Skill> masterSkills) {
        this.masterSkills = masterSkills;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(final String contactName) {
        this.contactName = contactName;
    }

    public List<Skill> getLearningSkills() {
        return learningSkills;
    }

    public void setLearningSkills(List<Skill> learningSkills) {
        this.learningSkills = learningSkills;
    }

    @OnJsonParseComplete
    void onParseComplete() {
        if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
            location = null;
        } else {
            location = new LatLng(latitude, longitude);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        UserParcelablePlease.writeToParcel(this, dest, flags);
    }

}
