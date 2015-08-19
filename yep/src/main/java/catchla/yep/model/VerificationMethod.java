package catchla.yep.model;

/**
 * Created by mariotaku on 15/5/26.
 */
public enum VerificationMethod {
    SMS("sms"),CALL("call");

    private final String method;

    VerificationMethod(final String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return method;
    }
}
