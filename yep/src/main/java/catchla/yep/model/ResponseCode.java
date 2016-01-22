package catchla.yep.model;

import org.mariotaku.restfu.RestConverter;
import org.mariotaku.restfu.http.HttpResponse;

import java.io.IOException;

/**
 * Created by mariotaku on 15/12/2.
 */
public class ResponseCode {

    public int getCode() {
        return code;
    }

    private final int code;

    public ResponseCode(final int code) {
        this.code = code;
    }

    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }

    public static class Converter implements RestConverter<HttpResponse, ResponseCode, YepException> {

        @Override
        public ResponseCode convert(final HttpResponse from) throws ConvertException, IOException, YepException {
            return new ResponseCode(from.getStatus());
        }
    }
}
