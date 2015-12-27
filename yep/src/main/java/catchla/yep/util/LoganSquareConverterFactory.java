package catchla.yep.util;

import android.support.v4.util.SimpleArrayMap;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.ParameterizedTypeTrojan;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import catchla.yep.model.AttachmentUpload;
import catchla.yep.model.ResponseCode;
import retrofit.Converter;

/**
 * Created by mariotaku on 15/12/1.
 */
public class LoganSquareConverterFactory extends Converter.Factory {

    private static final SimpleArrayMap<Type, Converter<ResponseBody, ?>> sResponseConverters = new SimpleArrayMap<>();
    private static final SimpleArrayMap<Type, Converter<?, RequestBody>> sRequestConverters = new SimpleArrayMap<>();

    static {
        sRequestConverters.put(AttachmentUpload.class, new AttachmentUpload.Converter());
        sResponseConverters.put(ResponseCode.class, new ResponseCode.Converter());
    }

    @Override
    public Converter<?, RequestBody> toRequestBody(final Type type, final Annotation[] annotations) {
        final Converter<?, RequestBody> converter = sRequestConverters.get(type);
        if (converter != null) return converter;
        return new ObjectSerializeConverter(type);
    }

    @Override
    public Converter<ResponseBody, ?> fromResponseBody(final Type type, final Annotation[] annotations) {
        final Converter<ResponseBody, ?> converter = sResponseConverters.get(type);
        if (converter != null) return converter;
        return new ObjectParseConverter(type);
    }

    private class ObjectParseConverter implements Converter<ResponseBody, Object> {
        private final Type type;

        public ObjectParseConverter(final Type type) {
            this.type = type;
        }

        @Override
        public Object convert(final ResponseBody value) throws IOException {
            return LoganSquare.parse(value.byteStream(), ParameterizedTypeTrojan.create(type));
        }
    }

    private class ObjectSerializeConverter implements Converter<Object, RequestBody> {
        private final Type type;

        public ObjectSerializeConverter(final Type type) {
            this.type = type;
        }

        @Override
        public RequestBody convert(final Object value) throws IOException {
            final JsonMapper jsonMapper = LoganSquare.mapperFor(ParameterizedTypeTrojan.create(type));
            //noinspection unchecked
            return RequestBody.create(MediaType.parse("application/json"), jsonMapper.serialize(value));
        }
    }
}
