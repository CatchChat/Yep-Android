package catchla.yep.model.util;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import catchla.yep.model.GithubRepo;

/**
 * Created by mariotaku on 15/5/23.
 */
public class GithubRepoListConverter implements TypeConverter<List<GithubRepo>> {

    @Override
    public List<GithubRepo> parse(final JsonParser jsonParser) throws IOException {
        final List<GithubRepo> skills = new ArrayList<>();
        skills.addAll(LoganSquare.mapperFor(GithubRepo.class).parseList(jsonParser));
        return skills;
    }

    @Override
    public void serialize(List<GithubRepo> object, String fieldName, boolean writeFieldNameForObject,
                          JsonGenerator jsonGenerator) throws IOException {
        if (object != null) {
            if (writeFieldNameForObject) {
                jsonGenerator.writeFieldName(fieldName);
            }
            LoganSquare.mapperFor(GithubRepo.class).serialize(object, jsonGenerator);
        }
    }
}
