package catchla.yep.model.util;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

import catchla.yep.model.GithubRepo;
import catchla.yep.model.InstagramMedia;
import io.realm.RealmList;

/**
 * Created by mariotaku on 15/5/23.
 */
public class InstagramMediaListConverter implements TypeConverter<RealmList<InstagramMedia>> {

    @Override
    public RealmList<InstagramMedia> parse(final JsonParser jsonParser) throws IOException {
        final RealmList<InstagramMedia> skills = new RealmList<>();
        skills.addAll(LoganSquare.mapperFor(InstagramMedia.class).parseList(jsonParser));
        return skills;
    }

    @Override
    public void serialize(RealmList<InstagramMedia> object, String fieldName, boolean writeFieldNameForObject,
                          JsonGenerator jsonGenerator) throws IOException {
        if (object != null) {
            if (writeFieldNameForObject) {
                jsonGenerator.writeFieldName(fieldName);
            }
            LoganSquare.mapperFor(InstagramMedia.class).serialize(object, jsonGenerator);
        }
    }
}
