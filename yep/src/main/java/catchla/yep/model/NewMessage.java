package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import org.mariotaku.restfu.http.SimpleValueMap;

import catchla.yep.util.JsonSerializer;
import catchla.yep.util.ParseUtils;

/**
 * Created by mariotaku on 15/6/12.
 */
public class NewMessage extends SimpleValueMap {

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
        put("attachments", JsonSerializer.serialize(attachment, (Class<T>) attachment.getClass()));
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
        File image;

        public ImageAttachment() {

        }

        public ImageAttachment(S3UploadToken token) {
            image = new File(token.getOptions().getKey());
        }

    }

}
