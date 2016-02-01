package catchla.yep.util;

import android.support.v4.util.SimpleArrayMap;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.ParameterizedTypeAccessor;

import org.mariotaku.restfu.RestConverter;
import org.mariotaku.restfu.http.ContentType;
import org.mariotaku.restfu.http.HttpResponse;
import org.mariotaku.restfu.http.mime.Body;
import org.mariotaku.restfu.http.mime.SimpleBody;
import org.mariotaku.restfu.http.mime.StringBody;

import java.io.IOException;
import java.lang.reflect.Type;

import catchla.yep.model.AttachmentUpload;
import catchla.yep.model.ResponseCode;
import catchla.yep.model.YepException;

/**
 * Created by mariotaku on 15/12/1.
 */
public class YepConverterFactory extends RestConverter.SimpleFactory<YepException> {

    private static final SimpleArrayMap<Type, RestConverter<HttpResponse, ?, YepException>> sResponseConverters = new SimpleArrayMap<>();
    private static final SimpleArrayMap<Type, RestConverter<?, Body, YepException>> sRequestConverters = new SimpleArrayMap<>();

    static {
        sRequestConverters.put(AttachmentUpload.class, new AttachmentUpload.Converter());
        sResponseConverters.put(ResponseCode.class, new ResponseCode.Converter());
    }


    @Override
    public RestConverter<HttpResponse, ?, YepException> forResponse(final Type toType) {
        final RestConverter<HttpResponse, ?, YepException> converter = sResponseConverters.get(toType);
        if (converter != null) return converter;
        return new ObjectParseConverter(toType);
    }

    @Override
    public RestConverter<?, Body, YepException> forRequest(final Type fromType) {
        final RestConverter<?, Body, YepException> converter = sRequestConverters.get(fromType);
        if (converter != null) return converter;
        if (SimpleBody.supports(fromType)) return new SimpleBodyConverter<>(fromType);
        if (LoganSquare.supports(ParameterizedTypeAccessor.create(fromType))) {
            return new ObjectSerializeConverter(fromType);
        }
        throw new RestConverter.UnsupportedTypeException(fromType);
    }


    private class ObjectParseConverter implements RestConverter<HttpResponse, Object, YepException> {
        private final Type type;

        public ObjectParseConverter(final Type type) {
            this.type = type;
        }

        @Override
        public Object convert(final HttpResponse response) throws ConvertException, IOException, YepException {
            return LoganSquare.parse(response.getBody().stream(), ParameterizedTypeAccessor.create(type));
        }
    }

    private class ObjectSerializeConverter implements RestConverter<Object, Body, YepException> {
        private final Type type;

        public ObjectSerializeConverter(final Type type) {
            this.type = type;
        }

        @Override
        public Body convert(final Object value) throws IOException {
            final JsonMapper jsonMapper = LoganSquare.mapperFor(ParameterizedTypeAccessor.create(type));
            //noinspection unchecked
            return new StringBody(jsonMapper.serialize(value), ContentType.parse("application/json"));
        }
    }
}
