package catchla.yep.model;

import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit.Call;
import retrofit.Response;

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

    public static class Converter implements retrofit.Converter<ResponseBody, ResponseCode> {
        @Override
        public ResponseCode convert(final ResponseBody value) throws IOException {
            return new ResponseCode(0);
        }
    }

    public static class CallAdapter implements retrofit.CallAdapter<ResponseCode> {
        @Override
        public Type responseType() {
            return ResponseCode.class;
        }

        @Override
        public <R> ResponseCode adapt(final Call<R> call) throws YepException {
            try {
                final Response<R> execute = call.execute();
                if (execute.isSuccess()) {
                    return new ResponseCode(execute.code());
                }
                throw new YepException(execute.message());
            } catch (IOException e) {
                throw new YepException(e);
            }
        }
    }
}
