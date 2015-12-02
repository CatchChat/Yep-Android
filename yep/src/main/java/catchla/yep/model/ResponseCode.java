package catchla.yep.model;

import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit.Call;

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
        public <R> ResponseCode adapt(final Call<R> call) throws Exception {
            return new ResponseCode(call.execute().code());
        }
    }
}
