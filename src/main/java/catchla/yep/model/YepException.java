package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.mariotaku.restfu.http.HttpRequest;
import org.mariotaku.restfu.http.HttpResponse;


/**
 * Created by mariotaku on 15/5/23.
 */
@JsonObject
public class YepException extends Exception {

    private HttpRequest request;
    private HttpResponse response;

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

    public void setRequest(final HttpRequest request) {
        this.request = request;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setResponse(final HttpResponse response) {
        this.response = response;
    }

    public HttpResponse getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "YepException{" +
                "request=" + request +
                ", response=" + response +
                ", error='" + error + '\'' +
                "} " + super.toString();
    }
}
