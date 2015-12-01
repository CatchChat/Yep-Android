package catchla.yep.util;

import com.squareup.okhttp.ResponseBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit.Converter;

/**
 * Created by mariotaku on 15/12/1.
 */
public class LoganSquareConverterFactory extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> fromResponseBody(final Type type, final Annotation[] annotations) {

        return super.fromResponseBody(type, annotations);
    }
}
