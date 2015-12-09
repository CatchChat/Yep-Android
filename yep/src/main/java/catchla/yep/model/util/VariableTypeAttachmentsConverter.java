package catchla.yep.model.util;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.jr.ob.JSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import catchla.yep.model.AppleMediaAttachment;
import catchla.yep.model.Attachment;
import catchla.yep.model.DribbbleAttachment;
import catchla.yep.model.FileAttachment;
import catchla.yep.model.GithubAttachment;
import catchla.yep.model.LocationAttachment;

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
                Map<String, Object> attachmentMap = JSON.std.mapFrom(jsonParser);
                final String kind = (String) attachmentMap.get("kind");
                Attachment instance;
                switch (kind) {
                    case "dribbble":
                        instance = LoganSquare.parse(JSON.std.asString(attachmentMap), DribbbleAttachment.class);
                        break;
                    case "github":
                        instance = LoganSquare.parse(JSON.std.asString(attachmentMap), GithubAttachment.class);
                        break;
                    case "location":
                        instance = LoganSquare.parse(JSON.std.asString(attachmentMap), LocationAttachment.class);
                        break;
                    case "apple_music":
                    case "apple_movie":
                    case "apple_ebook":
                        instance = LoganSquare.parse(JSON.std.asString(attachmentMap), AppleMediaAttachment.class);
                        break;
                    case "image":
                    case "video":
                    case "audio":
                        instance = LoganSquare.parse(JSON.std.asString(attachmentMap), FileAttachment.class);
                        break;
                    default:
                        instance = LoganSquare.parse(JSON.std.asString(attachmentMap), Attachment.class);
                        break;
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
        if (list != null) {
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
        } else {
            jsonGenerator.writeNull();
        }
    }
}
