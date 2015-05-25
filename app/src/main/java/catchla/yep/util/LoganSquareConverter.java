/*
 *                 Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package catchla.yep.util;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import org.mariotaku.restfu.Converter;
import org.mariotaku.restfu.Utils;
import org.mariotaku.restfu.http.ContentType;
import org.mariotaku.restfu.http.RestHttpResponse;
import org.mariotaku.restfu.http.mime.TypedData;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mariotaku on 15/5/5.
 */
public class LoganSquareConverter implements Converter {

    @Override
    public Object convert(RestHttpResponse response, Type type) throws Exception {
        final TypedData body = response.getBody();
        if (!response.isSuccessful()) {
            throw parseOrThrow(response, body.stream(), YepException.class);
        }
        final ContentType contentType = body.contentType();
        final InputStream stream = body.stream();
        try {
            if (type instanceof Class<?>) {
                final Class<?> cls = (Class<?>) type;
                final Object object = parseOrThrow(response, stream, cls);
                return object;
            } else if (type instanceof ParameterizedType) {
                final Type rawType = ((ParameterizedType) type).getRawType();
                if (rawType instanceof Class<?>) {
                    final Class<?> rawClass = (Class<?>) rawType;
                    if (ArrayList.class.isAssignableFrom(rawClass)) {
                        final Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
                        return new ArrayList<>(parseListOrThrow(response, stream, (Class<?>) elementType));
                    }
                }
            }
            throw new UnsupportedTypeException(type);
        } finally {
            Utils.closeSilently(stream);
        }
    }

    private static <T> T parseOrThrow(RestHttpResponse resp, InputStream stream, Class<T> cls) throws IOException, YepException {
        try {
            return LoganSquare.parse(stream, cls);
        } catch (JsonParseException e) {
            throw new YepException("Malformed JSON Data", resp);
        }
    }

    private static <T> List<T> parseListOrThrow(RestHttpResponse resp, InputStream stream, Class<T> elementCls) throws IOException, YepException {
        try {
            return LoganSquare.parseList(stream, elementCls);
        } catch (JsonParseException e) {
            throw new YepException("Malformed JSON Data", resp);
        }
    }


    private static class EnumConverter<T extends Enum<T>> implements TypeConverter<T> {
        private final Class<T> cls;

        EnumConverter(Class<T> cls) {
            this.cls = cls;
        }

        @SuppressWarnings({"unchecked", "TryWithIdenticalCatches"})
        @Override
        public T parse(JsonParser jsonParser) throws IOException {
            try {
                final Method method = cls.getMethod("parse", String.class);
                return (T) method.invoke(null, jsonParser.getValueAsString());
            } catch (NoSuchMethodException e) {
                return Enum.valueOf(cls, jsonParser.getValueAsString());
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void serialize(T object, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) {
            throw new UnsupportedOperationException();
        }

        public static <T extends Enum<T>> EnumConverter<T> get(Class<T> cls) {
            return new EnumConverter<>(cls);
        }
    }

    public static class UnsupportedTypeException extends UnsupportedOperationException {
        public UnsupportedTypeException(Type type) {
            super("Unsupported type " + type);
        }
    }
}
