package catchla.yep.model;

import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.jr.tree.JacksonJrSimpleTreeCodec;
import com.fasterxml.jackson.jr.tree.JsonBoolean;
import com.fasterxml.jackson.jr.tree.JsonNull;
import com.fasterxml.jackson.jr.tree.JsonNumber;
import com.fasterxml.jackson.jr.tree.JsonString;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import catchla.yep.model.util.VariableTypeAttachmentsConverter;
import catchla.yep.model.util.YepTimestampDateConverter;
import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 15/12/27.
 */
@SuppressWarnings("unused")
public class Topic$$JsonObjectMapper extends JsonMapper<Topic> {
    protected static final YepTimestampDateConverter CATCHLA_YEP_MODEL_UTIL_YEPTIMESTAMPDATECONVERTER = new YepTimestampDateConverter();

    protected static final VariableTypeAttachmentsConverter CATCHLA_YEP_MODEL_UTIL_VARIABLETYPEATTACHMENTSCONVERTER = new VariableTypeAttachmentsConverter();

    private static final JsonMapper<Circle> CATCHLA_YEP_MODEL_CIRCLE__JSONOBJECTMAPPER = LoganSquare.mapperFor(Circle.class);

    private static final JsonMapper<User> CATCHLA_YEP_MODEL_USER__JSONOBJECTMAPPER = LoganSquare.mapperFor(User.class);

    private static final JsonMapper<Skill> CATCHLA_YEP_MODEL_SKILL__JSONOBJECTMAPPER = LoganSquare.mapperFor(Skill.class);

    @Override
    public Topic parse(final JsonParser jsonParser) throws IOException {
        final Topic instance = new Topic();
        if (jsonParser.getCurrentToken() == null) {
            jsonParser.nextToken();
        }
        if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
            jsonParser.skipChildren();
            return null;
        }
        final TreeNode tree = JacksonJrSimpleTreeCodec.SINGLETON.readTree(jsonParser);
        final JsonString kind = (JsonString) tree.get("kind");
        if (kind != null) {
            instance.kind = kind.getValue();
        }
        final Iterator<String> fieldNames = tree.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            parseField(instance, fieldName, tree.get(fieldName));
        }
        return instance;
    }

    @Override
    public void parseField(final Topic instance, final String fieldName, final JsonParser jsonParser) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void parseField(final Topic instance, final String fieldName, @Nullable final TreeNode treeNode) throws IOException {
        if (treeNode == null || treeNode instanceof JsonNull) return;
        if ("allow_comment".equals(fieldName)) {
            instance.setAllowComment(treeNode == JsonBoolean.TRUE);
        } else if ("attachments".equals(fieldName)) {
            final String kind = instance.getKind();
            if (kind != null) {
                final JsonMapper<? extends Attachment> mapperForKind = VariableTypeAttachmentsConverter.getMapperForKind(kind);
                //noinspection unchecked
                instance.attachments = (List<Attachment>) mapperForKind.parseList(Utils.getJsonParser(treeNode));
            } else {
                instance.attachments = CATCHLA_YEP_MODEL_UTIL_VARIABLETYPEATTACHMENTSCONVERTER.parse(Utils.getJsonParser(treeNode));
            }
        } else if ("body".equals(fieldName)) {
            instance.body = ((JsonString) treeNode).getValue();
        } else if ("circle".equals(fieldName)) {
            instance.circle = CATCHLA_YEP_MODEL_CIRCLE__JSONOBJECTMAPPER.parse(Utils.getJsonParser(treeNode));
        } else if ("created_at".equals(fieldName)) {
            instance.setCreatedAt(CATCHLA_YEP_MODEL_UTIL_YEPTIMESTAMPDATECONVERTER.parse(Utils.getJsonParser(treeNode)));
        } else if ("id".equals(fieldName)) {
            instance.id = ((JsonString) treeNode).getValue();
        } else if ("message_count".equals(fieldName)) {
            instance.messageCount = ((JsonNumber) treeNode).getValue().intValue();
        } else if ("skill".equals(fieldName)) {
            instance.setSkill(CATCHLA_YEP_MODEL_SKILL__JSONOBJECTMAPPER.parse(Utils.getJsonParser(treeNode)));
        } else if ("updated_at".equals(fieldName)) {
            instance.setUpdatedAt(CATCHLA_YEP_MODEL_UTIL_YEPTIMESTAMPDATECONVERTER.parse(Utils.getJsonParser(treeNode)));
        } else if ("user".equals(fieldName)) {
            instance.setUser(CATCHLA_YEP_MODEL_USER__JSONOBJECTMAPPER.parse(Utils.getJsonParser(treeNode)));
        } else if ("kind".equals(fieldName)) {
            instance.kind = ((JsonString) treeNode).getValue();
        }
    }

    @Override
    public void serialize(final Topic object, final JsonGenerator jsonGenerator, final boolean writeStartAndEnd) throws IOException {
        if (writeStartAndEnd) {
            jsonGenerator.writeStartObject();
        }
        jsonGenerator.writeBooleanField("allow_comment", object.isAllowComment());
        CATCHLA_YEP_MODEL_UTIL_VARIABLETYPEATTACHMENTSCONVERTER.serialize(object.getAttachments(), "attachments", true, jsonGenerator);
        if (object.getBody() != null) {
            jsonGenerator.writeStringField("body", object.getBody());
        }
        if (object.getCircle() != null) {
            jsonGenerator.writeFieldName("circle");
            CATCHLA_YEP_MODEL_CIRCLE__JSONOBJECTMAPPER.serialize(object.getCircle(), jsonGenerator, true);
        }
        CATCHLA_YEP_MODEL_UTIL_YEPTIMESTAMPDATECONVERTER.serialize(object.getCreatedAt(), "created_at", true, jsonGenerator);
        if (object.getId() != null) {
            jsonGenerator.writeStringField("id", object.getId());
        }
        if (object.getKind() != null) {
            jsonGenerator.writeStringField("kind", object.getKind());
        }
        jsonGenerator.writeNumberField("message_count", object.getMessageCount());
        if (object.getSkill() != null) {
            jsonGenerator.writeFieldName("skill");
            CATCHLA_YEP_MODEL_SKILL__JSONOBJECTMAPPER.serialize(object.getSkill(), jsonGenerator, true);
        }
        CATCHLA_YEP_MODEL_UTIL_YEPTIMESTAMPDATECONVERTER.serialize(object.getUpdatedAt(), "updated_at", true, jsonGenerator);
        if (object.getUser() != null) {
            jsonGenerator.writeFieldName("user");
            CATCHLA_YEP_MODEL_USER__JSONOBJECTMAPPER.serialize(object.getUser(), jsonGenerator, true);
        }
        if (object.kind != null) {
            jsonGenerator.writeStringField("kind", object.kind);
        }
        if (writeStartAndEnd) {
            jsonGenerator.writeEndObject();
        }
    }
}
