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

package catchla.yep.model;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.ParameterizedType;
import com.bluelinelabs.logansquare.util.SimpleArrayMap;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;

@SuppressWarnings("unused")
public final class ResponseList$$JsonObjectMapper<T> extends JsonMapper<ResponseList<T>> {
    private final JsonMapper<T> elementJsonMapper;

    public ResponseList$$JsonObjectMapper(ParameterizedType type, ParameterizedType elementType,
                                          SimpleArrayMap<ParameterizedType, JsonMapper> partialMappers) {
        partialMappers.put(type, this);
        //noinspection unchecked
        elementJsonMapper = LoganSquare.mapperFor(elementType, partialMappers);
    }

    @Override
    public ResponseList<T> parse(JsonParser jsonParser) throws IOException {
        if (jsonParser.getCurrentToken() == null) {
            jsonParser.nextToken();
        }
        ResponseList<T> instance = new ResponseList<>();
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jsonParser.getCurrentName();
            jsonParser.nextToken();
            parseField(instance, fieldName, jsonParser);
            jsonParser.skipChildren();
        }
        return instance;
    }

    @Override
    public void parseField(ResponseList<T> instance, String fieldName, JsonParser jsonParser) throws IOException {
        switch (fieldName) {
            case "users":
            case "circles":
            case "messages":
            case "friendships":
            case "categories": {
                instance.addAll(elementJsonMapper.parseList(jsonParser));
                break;
            }
            case "current_page": {
                instance.setCurrentPage(jsonParser.getValueAsInt());
                break;
            }
            case "perPage": {
                instance.setPerPage(jsonParser.getValueAsInt());
                break;
            }
            case "count": {
                instance.setCount(jsonParser.getValueAsInt());
                break;
            }
        }
    }

    @Override
    public void serialize(ResponseList<T> object, JsonGenerator jsonGenerator, boolean writeStartAndEnd) throws IOException {
        if (object == null) {
            jsonGenerator.writeNull();
            return;
        }
        if (writeStartAndEnd) {
            jsonGenerator.writeStartArray();
        }
        for (T t : object) {
            elementJsonMapper.serialize(t, jsonGenerator, true);
        }
        if (writeStartAndEnd) {
            jsonGenerator.writeEndArray();
        }
    }
}
