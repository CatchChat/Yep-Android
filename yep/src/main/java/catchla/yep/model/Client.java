package catchla.yep.model;

/**
 * Created by mariotaku on 15/5/23.
 */
public enum Client {

    OFFICIAL("0"), COMPANY("1"), LOCAL("2");

    private final String value;

    Client(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
