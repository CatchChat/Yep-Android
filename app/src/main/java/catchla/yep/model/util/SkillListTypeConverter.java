package catchla.yep.model.util;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

import catchla.yep.model.Skill;
import io.realm.RealmList;

/**
 * Created by mariotaku on 15/5/23.
 */
public class SkillListTypeConverter implements TypeConverter<RealmList<Skill>> {

    @Override
    public RealmList<Skill> parse(final JsonParser jsonParser) throws IOException {
        final RealmList<Skill> skills = new RealmList<>();
        skills.addAll(LoganSquare.mapperFor(Skill.class).parseList(jsonParser));
        return skills;
    }

    @Override
    public void serialize(RealmList<Skill> object, String fieldName, boolean writeFieldNameForObject,
                          JsonGenerator jsonGenerator) throws IOException {
        throw new UnsupportedOperationException();
    }
}
