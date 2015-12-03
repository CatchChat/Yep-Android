package catchla.yep.model.util;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import catchla.yep.model.Attachment;
import catchla.yep.model.BasicAttachment;
import catchla.yep.model.DribbbleAttachment;

/**
 * Created by mariotaku on 15/11/26.
 */
public class VariableTypeAttachmentsConverter implements TypeConverter<List<Attachment>> {
    @Override
    public List<Attachment> parse(final JsonParser jsonParser) throws IOException {
        List<Attachment> list = new ArrayList<>();
        if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {

                if (jsonParser.getCurrentToken() == null) {
                    jsonParser.nextToken();
                }
                if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
                    jsonParser.skipChildren();
                    continue;
                }
                Attachment instance = null;
                JsonMapper<Attachment> mapper = null;
                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                    String fieldName = jsonParser.getCurrentName();
                    jsonParser.nextToken();
                    if ("kind".equals(fieldName)) {
                        final String kind = jsonParser.getValueAsString();
                        if ("dribbble".equals(kind)) {
                            instance = new DribbbleAttachment();
                        } else {
                            instance = new BasicAttachment();
                        }
                        instance.setKind(kind);
                        //noinspection unchecked
                        mapper = (JsonMapper<Attachment>) LoganSquare.mapperFor(instance.getClass());
                    } else if (instance != null && mapper != null) {
                        mapper.parseField(instance, fieldName, jsonParser);
                    }
                    jsonParser.skipChildren();
                }
                if (instance != null) {
                    list.add(instance);
                }
            }
        }
        return list;
    }

    @Override
    public void serialize(final List<Attachment> list, final String fieldName,
                          final boolean writeFieldNameForObject, final JsonGenerator jsonGenerator) throws IOException {
        if (writeFieldNameForObject) {
            jsonGenerator.writeFieldName(fieldName);
        }
        jsonGenerator.writeStartArray();
        for (Attachment item : list) {
            if (item != null) {
                //noinspection unchecked
                final Class<Attachment> cls = (Class<Attachment>) item.getClass();
                final JsonMapper<Attachment> jsonMapper = LoganSquare.mapperFor(cls);
                jsonMapper.serialize(item, jsonGenerator, true);
            } else {
                jsonGenerator.writeNull();
            }
        }
        jsonGenerator.writeEndArray();
    }
}
