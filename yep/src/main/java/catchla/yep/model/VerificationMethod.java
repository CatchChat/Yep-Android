package catchla.yep.model;

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

import java.util.Locale;

/**
 * Created by mariotaku on 15/5/26.
 */
public enum VerificationMethod {
    SMS("sms"), CALL("call");

    private final String method;

    VerificationMethod(final String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return method;
    }

    public static class JsonConverter extends StringBasedTypeConverter<VerificationMethod> {
        @Override
        public VerificationMethod getFromString(final String string) {
            return VerificationMethod.valueOf(string.toLowerCase(Locale.US));
        }

        @Override
        public String convertToString(final VerificationMethod object) {
            return object.method;
        }
    }
}
