package catchla.yep.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by mariotaku on 15/5/8.
 */
public class User extends RealmObject {

    @SerializedName("master_skills")
    private RealmList<Skill> masterSkills;
    @SerializedName("learning_skills")
    private RealmList<Skill> learningSkills;
    @SerializedName("id")
    private String id;
    @SerializedName("username")
    private String username;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("introduction")
    private String introduction;
    @SerializedName("avatar_url")
    private String avatarUrl;

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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

    public RealmList<Skill> getMasterSkills() {
        return masterSkills;
    }

    public void setMasterSkills(RealmList<Skill> masterSkills) {
        this.masterSkills = masterSkills;
    }

    public RealmList<Skill> getLearningSkills() {
        return learningSkills;
    }

    public void setLearningSkills(RealmList<Skill> learningSkills) {
        this.learningSkills = learningSkills;
    }
}
