package catchla.yep.model.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.simple.tree.JsonString;
import com.fasterxml.jackson.simple.tree.SimpleTreeCodec;

import org.mariotaku.library.objectcursor.converter.CursorFieldConverter;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import catchla.yep.BuildConfig;
import catchla.yep.Constants;
import catchla.yep.model.AppleMediaAttachment;
import catchla.yep.model.Attachment;
import catchla.yep.model.DribbbleAttachment;
import catchla.yep.model.FileAttachment;
import catchla.yep.model.GithubAttachment;
import catchla.yep.model.LocationAttachment;
import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 15/11/26.
 */
public class VariableTypeAttachmentsConverter implements TypeConverter<List<Attachment>>,
        CursorFieldConverter<List<Attachment>>, Constants {

    SimpleTreeCodec codec = new SimpleTreeCodec();

    @Override
    public List<Attachment> parse(final JsonParser jsonParser) throws IOException {
        List<Attachment> list = new ArrayList<>();
        if (jsonParser.getCurrentToken() == null) {
            jsonParser.nextToken();
        }
        if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {
            TreeNode treeNode = codec.readTree(jsonParser);
            for (int i = 0; i < treeNode.size(); i++) {
                TreeNode child = treeNode.get(i);
                final JsonString kindNode = (JsonString) child.get("kind");
                if (kindNode == null) {
                    codec.writeTree(LoganSquare.JSON_FACTORY.createGenerator(System.err), child);
                    continue;
                }
                final String kind = kindNode.getValue();
                Attachment instance;
                switch (kind) {
                    case "dribbble":
                        instance = LoganSquare.mapperFor(DribbbleAttachment.class).parse(codec.treeAsTokens(child));
                        break;
                    case "github":
                        instance = LoganSquare.mapperFor(GithubAttachment.class).parse(codec.treeAsTokens(child));
                        break;
                    case "location":
                        instance = LoganSquare.mapperFor(LocationAttachment.class).parse(codec.treeAsTokens(child));
                        break;
                    case "apple_music":
                    case "apple_movie":
                    case "apple_ebook":
                        instance = LoganSquare.mapperFor(AppleMediaAttachment.class).parse(codec.treeAsTokens(child));
                        break;
                    case "image":
                    case "video":
                    case "audio":
                        instance = LoganSquare.mapperFor(FileAttachment.class).parse(codec.treeAsTokens(child));
                        break;
                    default:
                        instance = LoganSquare.mapperFor(Attachment.class).parse(codec.treeAsTokens(child));
                        break;
                }
                if (instance != null) {
                    list.add(instance);
                }
            }
        }
        return list;
    }

    @Override
    public void serialize(final List<Attachment> list, final String fieldName,
                          final boolean writeFieldNameForObject, final JsonGenerator jsonGenerator) throws IOException {
        if (writeFieldNameForObject) {
            jsonGenerator.writeFieldName(fieldName);
        }
        if (list != null) {
            jsonGenerator.writeStartArray();
            for (Attachment item : list) {
                if (item != null) {
                    //noinspection unchecked
                    final Class<Attachment> cls = (Class<Attachment>) item.getClass();
                    final JsonMapper<Attachment> jsonMapper = LoganSquare.mapperFor(cls);
                    jsonMapper.serialize(item, jsonGenerator, true);
                } else {
                    jsonGenerator.writeNull();
                }
            }
            jsonGenerator.writeEndArray();
        } else {
            jsonGenerator.writeNull();
        }
    }

    @Override
    public List<Attachment> parseField(final Cursor cursor, final int columnIndex, final ParameterizedType fieldType) {
        final String json = cursor.getString(columnIndex);
        if (TextUtils.isEmpty(json)) return null;
        try {
            return parse(LoganSquare.JSON_FACTORY.createParser(json));
        } catch (IOException e) {
            if (e instanceof JsonProcessingException && BuildConfig.DEBUG) {
                Log.w(LOGTAG, e);
            }
            return null;
        }
    }

    @Override
    public void writeField(final ContentValues values, final List<Attachment> object,
                           final String columnName, final ParameterizedType fieldType) {
        final StringWriter sw = new StringWriter();
        try {
            serialize(object, null, false, LoganSquare.JSON_FACTORY.createGenerator(sw));
            values.put(columnName, sw.toString());
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.w(LOGTAG, e);
            }
            // Ignore
        } finally {
            Utils.closeSilently(sw);
        }
    }
}
