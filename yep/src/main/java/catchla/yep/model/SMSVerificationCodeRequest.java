package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import catchla.yep.model.iface.JsonRequestBody;

/**
 * Created by mariotaku on 15/12/2.
 */
@JsonObject
public class SMSVerificationCodeRequest implements JsonRequestBody {
    @JsonField(name = "mobile")
    String mobile;
    @JsonField(name = "phone_code")
    String phoneCode;
    @JsonField(name = "method", typeConverter = VerificationMethod.JsonConverter.class)
    VerificationMethod method;

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

    public VerificationMethod getMethod() {
        return method;
    }

    public void setMethod(final VerificationMethod method) {
        this.method = method;
    }
}
