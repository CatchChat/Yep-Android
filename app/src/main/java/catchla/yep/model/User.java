package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import catchla.yep.model.util.SkillListTypeConverter;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by mariotaku on 15/5/8.
 */
@JsonObject
public class User extends RealmObject {

    @JsonField(name = "master_skills", typeConverter = SkillListTypeConverter.class)
    private RealmList<Skill> masterSkills;
    @JsonField(name = "learning_skills", typeConverter = SkillListTypeConverter.class)
    private RealmList<Skill> learningSkills;
    @JsonField(name = "id")
    private String id;
    @JsonField(name = "username")
    private String username;
    @JsonField(name = "nickname")
    private String nickname;
    @JsonField(name = "introduction")
    private String introduction;
    @JsonField(name = "avatar_url")
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
