package catchla.yep.model.util;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

import catchla.yep.model.DribbbleShot;
import catchla.yep.model.DribbbleShots;
import catchla.yep.model.Skill;
import io.realm.RealmList;

/**
 * Created by mariotaku on 15/5/23.
 */
public class DribbbleShotListConverter implements TypeConverter<RealmList<DribbbleShot>> {

    @Override
    public RealmList<DribbbleShot> parse(final JsonParser jsonParser) throws IOException {
        final RealmList<DribbbleShot> skills = new RealmList<>();
        skills.addAll(LoganSquare.mapperFor(DribbbleShot.class).parseList(jsonParser));
        return skills;
    }

    @Override
    public void serialize(RealmList<DribbbleShot> object, String fieldName, boolean writeFieldNameForObject,
                          JsonGenerator jsonGenerator) throws IOException {
        if (object != null) {
            if (writeFieldNameForObject) {
                jsonGenerator.writeFieldName(fieldName);
            }
            LoganSquare.mapperFor(DribbbleShot.class).serialize(object, jsonGenerator);
        }
    }
}
