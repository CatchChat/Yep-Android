package catchla.yep.model.util;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import catchla.yep.model.Skill;

/**
 * Created by mariotaku on 15/5/23.
 */
public class SkillListTypeConverter implements TypeConverter<List<Skill>> {

    @Override
    public List<Skill> parse(final JsonParser jsonParser) throws IOException {
        final List<Skill> skills = new ArrayList<>();
        skills.addAll(LoganSquare.mapperFor(Skill.class).parseList(jsonParser));
        return skills;
    }

    @Override
    public void serialize(List<Skill> object, String fieldName, boolean writeFieldNameForObject,
                          JsonGenerator jsonGenerator) throws IOException {
        if (object != null) {
            if (writeFieldNameForObject) {
                jsonGenerator.writeFieldName(fieldName);
            }
            LoganSquare.mapperFor(Skill.class).serialize(object, jsonGenerator);
        }
    }
}
