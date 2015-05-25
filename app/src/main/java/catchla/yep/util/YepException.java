package catchla.yep.util;

import org.mariotaku.restfu.http.RestHttpResponse;

/**
 * Created by mariotaku on 15/5/23.
 */
public class YepException extends Exception {
    public YepException(final String message, final RestHttpResponse resp) {
        super(message);
    }

    public YepException(final String message) {
        super(message);
    }

    public YepException(final Throwable cause) {
        super(cause);
    }
}
