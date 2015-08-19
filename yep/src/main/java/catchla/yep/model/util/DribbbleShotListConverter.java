package catchla.yep.model.util;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import catchla.yep.model.DribbbleShot;

/**
 * Created by mariotaku on 15/5/23.
 */
public class DribbbleShotListConverter implements TypeConverter<List<DribbbleShot>> {

    @Override
    public List<DribbbleShot> parse(final JsonParser jsonParser) throws IOException {
        final List<DribbbleShot> skills = new ArrayList<>();
        skills.addAll(LoganSquare.mapperFor(DribbbleShot.class).parseList(jsonParser));
        return skills;
    }

    @Override
    public void serialize(List<DribbbleShot> object, String fieldName, boolean writeFieldNameForObject,
                          JsonGenerator jsonGenerator) throws IOException {
        if (object != null) {
            if (writeFieldNameForObject) {
                jsonGenerator.writeFieldName(fieldName);
            }
            LoganSquare.mapperFor(DribbbleShot.class).serialize(object, jsonGenerator);
        }
    }
}
