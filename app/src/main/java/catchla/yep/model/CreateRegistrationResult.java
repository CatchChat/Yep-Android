package catchla.yep.model;


import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by mariotaku on 15/2/4.
 */
@JsonObject
public class CreateRegistrationResult {

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
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

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public boolean isSentSms() {
        return sentSms;
    }

    public void setSentSms(final boolean sentSms) {
        this.sentSms = sentSms;
    }

    @JsonField(name = "username")
    private String username;
    @JsonField(name = "nickname")
    private String nickname;
    @JsonField(name = "mobile")
    private String mobile;
    @JsonField(name = "phone_code")
    private String phoneCode;
    @JsonField(name = "state")
    private String state;
    @JsonField(name = "sent_sms")
    private boolean sentSms;

    @Override
    public String toString() {
        return "CreateRegistrationResult{" +
                "username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", phoneCode='" + phoneCode + '\'' +
                ", state='" + state + '\'' +
                ", sentSms=" + sentSms +
                '}';
    }

}
