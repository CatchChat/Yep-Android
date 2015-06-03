package catchla.yep.model.util;

import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;

import catchla.yep.model.DribbbleShotImage;
import io.realm.RealmList;

/**
 * Created by mariotaku on 15/6/2.
 */
public class DribbbleShotImageConverter implements TypeConverter<RealmList<DribbbleShotImage>> {
    @Override
    public RealmList<DribbbleShotImage> parse(final JsonParser jsonParser) throws IOException {
        final RealmList<DribbbleShotImage> providers = new RealmList<>();
        if (jsonParser.getCurrentToken() == null) {
            jsonParser.nextToken();
        }
        if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
            jsonParser.skipChildren();
            return null;
        }
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            final String resolution = jsonParser.getCurrentName();
            jsonParser.nextToken();
            providers.add(new DribbbleShotImage(resolution, jsonParser.getValueAsString()));
            jsonParser.skipChildren();
        }
        return providers;
    }

    @Override
    public void serialize(final RealmList<DribbbleShotImage> object, String fieldName, boolean writeFieldNameForObject,
                          JsonGenerator jsonGenerator) throws IOException {
        if (object != null) {
            if (writeFieldNameForObject) {
                jsonGenerator.writeFieldName(fieldName);
            }
            jsonGenerator.writeStartObject();
            for (DribbbleShotImage provider : object) {
                jsonGenerator.writeFieldName(provider.getResolution());
                jsonGenerator.writeString(provider.getUrl());
            }
            jsonGenerator.writeEndObject();
        }
    }
}
