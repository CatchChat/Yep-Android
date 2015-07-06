package catchla.yep.model.util;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import catchla.yep.model.InstagramMedia;

/**
 * Created by mariotaku on 15/5/23.
 */
public class InstagramMediaListConverter implements TypeConverter<List<InstagramMedia>> {

    @Override
    public List<InstagramMedia> parse(final JsonParser jsonParser) throws IOException {
        final List<InstagramMedia> skills = new ArrayList<>();
        skills.addAll(LoganSquare.mapperFor(InstagramMedia.class).parseList(jsonParser));
        return skills;
    }

    @Override
    public void serialize(List<InstagramMedia> object, String fieldName, boolean writeFieldNameForObject,
                          JsonGenerator jsonGenerator) throws IOException {
        if (object != null) {
            if (writeFieldNameForObject) {
                jsonGenerator.writeFieldName(fieldName);
            }
            LoganSquare.mapperFor(InstagramMedia.class).serialize(object, jsonGenerator);
        }
    }
}
