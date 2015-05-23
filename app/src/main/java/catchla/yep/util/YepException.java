package catchla.yep.util;

import org.mariotaku.simplerestapi.http.RestHttpResponse;

/**
 * Created by mariotaku on 15/5/23.
 */
public class YepException extends Exception{
    public YepException(final String message, final RestHttpResponse resp) {

    }

    public YepException(final String message) {

    }
}
