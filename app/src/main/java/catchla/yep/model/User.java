package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import catchla.yep.model.util.ProviderConverter;
import catchla.yep.model.util.SkillListTypeConverter;

/**
 * Created by mariotaku on 15/5/8.
 */
@JsonObject
public class User {

    @JsonField(name = "master_skills", typeConverter = SkillListTypeConverter.class)
    private List<Skill> masterSkills;
    @JsonField(name = "learning_skills", typeConverter = SkillListTypeConverter.class)
    private List<Skill> learningSkills;
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
    @JsonField(name = "mobile")
    private String mobile;
    @JsonField(name = "phone_code")
    private String phoneCode;
    @JsonField(name = "contact_name")
    private String contactName;
    @JsonField(name = "providers", typeConverter = ProviderConverter.class)
    private List<Provider> providers;

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

}
