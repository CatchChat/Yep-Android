package catchla.yep.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.mariotaku.restfu.http.ContentType;
import org.mariotaku.restfu.http.SimpleValueMap;
import org.mariotaku.restfu.http.mime.StringTypedData;
import org.mariotaku.restfu.http.mime.TypedData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import catchla.yep.model.util.ValueMapJsonMapper;
import catchla.yep.util.ParseUtils;

/**
 * Created by mariotaku on 15/6/12.
 */
public class NewMessage extends SimpleValueMap {

    private static final ValueMapJsonMapper<NewMessage> sMapper = new ValueMapJsonMapper<>();

    private String conversationId;
    private long createdAt;
    private Circle circle;
    private User sender;

    public NewMessage recipientId(String recipientId) {
        put("recipient_id", recipientId);
        return this;
    }

    public NewMessage recipientType(String recipientType) {
        put("recipient_type", recipientType);
        return this;
    }

    public NewMessage mediaType(String mediaType) {
        put("media_type", mediaType);
        return this;
    }

    public NewMessage textContent(String textContent) {
        put("text_content", textContent);
        return this;
    }

    public NewMessage conversationId(final String conversationId) {
        this.conversationId = conversationId;
        return this;
    }


    public NewMessage createdAt(final long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public NewMessage parentId(String parentId) {
        put("parent_id", parentId);
        return this;
    }

    public NewMessage location(double latitude, double longitude) {
        put("latitude", latitude);
        put("longitude", longitude);
        return this;
    }

    public String conversationId() {
        return conversationId;
    }

    public long createdAt() {
        return createdAt;
    }

    public String parentId() {
        return ParseUtils.parseString(get("parent_id"), null);
    }

    public String recipientId() {
        return ParseUtils.parseString(get("recipient_id"), null);
    }

    public String recipientType() {
        return ParseUtils.parseString(get("recipient_type"), null);
    }

    public void circle(final Circle circle) {
        this.circle = circle;
    }

    public Circle circle() {
        return circle;
    }

    public void sender(final User sender) {
        this.sender = sender;
    }

    public User sender() {
        return sender;
    }

    public String textContent() {
        return ParseUtils.parseString(get("text_content"), null);
    }

    public <T extends Attachment> void attachment(final T attachment) {
        if (attachment == null) return;
        //noinspection unchecked
        put("attachments", attachment);
    }

    public String mediaType() {
        return ParseUtils.parseString(get("media_type"));
    }

    public double latitude() {
        return ParseUtils.parseDouble(ParseUtils.parseString(get("latitude")), Double.NaN);
    }

    public double longitude() {
        return ParseUtils.parseDouble(ParseUtils.parseString(get("longitude")), Double.NaN);
    }

    public JsonBody toJson() {
        try {
            final String json = sMapper.serialize(this);
            return new JsonBody(json);
        } catch (IOException e) {
            return null;
        }
    }

    public static final class JsonBody implements TypedData {

        private final StringTypedData delegated;

        @Override
        @Nullable
        public ContentType contentType() {
            return delegated.contentType();
        }

        @Override
        public String contentEncoding() {
            return delegated.contentEncoding();
        }

        @Override
        public long length() throws IOException {
            return delegated.length();
        }

        @Override
        public void writeTo(@NonNull final OutputStream os) throws IOException {
            delegated.writeTo(os);
        }

        @Override
        @NonNull
        public InputStream stream() throws IOException {
            return delegated.stream();
        }

        @Override
        public void close() throws IOException {
            delegated.close();
        }

        private JsonBody(String json) {
            delegated = new StringTypedData(json,
                    ContentType.parse("application/json").charset(Charset.defaultCharset()));
        }

    }

    public interface Attachment {

        @JsonObject
        class File {
            @JsonField(name = "file")
            String file;

            public File() {
            }

            public File(final String file) {
                this.file = file;
            }
        }
    }

    @JsonObject
    public static class ImageAttachment implements Attachment {

        @JsonField(name = "image")
        File[] image;

        public void setMetadata(final Message.Attachment.ImageMetadata[] metadata) {
            this.metadata = metadata;
        }

        @JsonField(name = "metadata")
        Message.Attachment.ImageMetadata[] metadata;

        public ImageAttachment() {

        }

        public ImageAttachment(S3UploadToken token) {
            image = new File[]{new File(token.getOptions().getKey())};
        }

    }

}
