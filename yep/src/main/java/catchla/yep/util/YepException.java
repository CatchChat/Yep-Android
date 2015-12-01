package catchla.yep.util;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


/**
 * Created by mariotaku on 15/5/23.
 */
@JsonObject
public class YepException extends Exception {

    private Request request;
    private Response response;

    @JsonField(name = "error")
    String error;

    public String getError() {
        return error;
    }

    public YepException() {

    }

    public YepException(final String message) {
        super(message);
    }

    public YepException(final Throwable cause) {
        super(cause);
    }

    public void setRequest(final Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public void setResponse(final Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }
}
