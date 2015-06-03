package catchla.yep.model.util;

import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;

import catchla.yep.model.Provider;
import io.realm.RealmList;

/**
 * Created by mariotaku on 15/6/2.
 */
public class ProviderConverter implements TypeConverter<RealmList<Provider>> {
    @Override
    public RealmList<Provider> parse(final JsonParser jsonParser) throws IOException {
        final RealmList<Provider> providers = new RealmList<>();
        if (jsonParser.getCurrentToken() == null) {
            jsonParser.nextToken();
        }
        if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
            jsonParser.skipChildren();
            return null;
        }
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            final String name = jsonParser.getCurrentName();
            jsonParser.nextToken();
            providers.add(new Provider(name, jsonParser.getValueAsBoolean()));
            jsonParser.skipChildren();
        }
        return providers;
    }

    @Override
    public void serialize(final RealmList<Provider> object, String fieldName, boolean writeFieldNameForObject,
                          JsonGenerator jsonGenerator) throws IOException {
        if (object != null) {
            if (writeFieldNameForObject) {
                jsonGenerator.writeFieldName(fieldName);
            }
            jsonGenerator.writeStartObject();
            for (Provider provider : object) {
                jsonGenerator.writeFieldName(provider.getName());
                jsonGenerator.writeBoolean(provider.isSupported());
            }
            jsonGenerator.writeEndObject();
        }
    }
}
