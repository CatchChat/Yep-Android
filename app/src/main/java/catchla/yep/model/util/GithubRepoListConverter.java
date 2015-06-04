package catchla.yep.model.util;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

import catchla.yep.model.GithubRepo;
import catchla.yep.model.Skill;
import io.realm.RealmList;

/**
 * Created by mariotaku on 15/5/23.
 */
public class GithubRepoListConverter implements TypeConverter<RealmList<GithubRepo>> {

    @Override
    public RealmList<GithubRepo> parse(final JsonParser jsonParser) throws IOException {
        final RealmList<GithubRepo> skills = new RealmList<>();
        skills.addAll(LoganSquare.mapperFor(GithubRepo.class).parseList(jsonParser));
        return skills;
    }

    @Override
    public void serialize(RealmList<GithubRepo> object, String fieldName, boolean writeFieldNameForObject,
                          JsonGenerator jsonGenerator) throws IOException {
        if (object != null) {
            if (writeFieldNameForObject) {
                jsonGenerator.writeFieldName(fieldName);
            }
            LoganSquare.mapperFor(GithubRepo.class).serialize(object, jsonGenerator);
        }
    }
}
