package catchla.yep.util;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.mariotaku.restfu.http.RestHttpRequest;
import org.mariotaku.restfu.http.RestHttpResponse;

/**
 * Created by mariotaku on 15/5/23.
 */
@JsonObject
public class YepException extends Exception {

    private RestHttpRequest request;
    private RestHttpResponse response;

    @JsonField(name = "error")
    String error;

    public String getError() {
        return error;
    }

    public YepException() {

    }


    public YepException(final String message, final RestHttpResponse resp) {
        super(message);
    }

    public YepException(final String message) {
        super(message);
    }

    public YepException(final Throwable cause) {
        super(cause);
    }

    public void setRequest(final RestHttpRequest request) {
        this.request = request;
    }

    public RestHttpRequest getRequest() {
        return request;
    }

    public void setResponse(final RestHttpResponse response) {
        this.response = response;
    }

    public RestHttpResponse getResponse() {
        return response;
    }
}
