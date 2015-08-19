package catchla.yep.model;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import catchla.yep.model.util.NaNDoubleConverter;
import catchla.yep.model.util.YepTimestampDateConverter;
import catchla.yep.provider.YepDataStore.Messages;
import catchla.yep.util.JsonSerializer;

/**
 * Created by mariotaku on 15/5/12.
 */
@JsonObject
public class Message {

    @JsonField(name = "latitude", typeConverter = NaNDoubleConverter.class)
    double latitude = Double.NaN;
    @JsonField(name = "longitude", typeConverter = NaNDoubleConverter.class)
    double longitude = Double.NaN;
    @JsonField(name = "id")
    private String id;
    @JsonField(name = "recipient_id")
    private String recipientId;
    @JsonField(name = "parent_id")
    private String parentId;
    @JsonField(name = "text_content")
    private String textContent;
    @JsonField(name = "created_at", typeConverter = YepTimestampDateConverter.class)
    private Date createdAt;
    @JsonField(name = "sender")
    private User sender;
    @JsonField(name = "recipient_type")
    private String recipientType;
    @JsonField(name = "media_type")
    private String mediaType;
    @JsonField(name = "circle")
    private Circle circle;
    @JsonField(name = "conversation_id")
    private String conversationId;
    @JsonField(name = "outgoing")
    private boolean outgoing;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(final String mediaType) {
        this.mediaType = mediaType;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(final String recipientType) {
        this.recipientType = recipientType;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(final Circle circle) {
        this.circle = circle;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(final String conversationId) {
        this.conversationId = conversationId;
    }

    public boolean isOutgoing() {
        return outgoing;
    }

    public void setOutgoing(final boolean outgoing) {
        this.outgoing = outgoing;
    }

    public interface RecipientType {
        String USER = "User";
        String CIRCLE = "Circle";
    }

    public interface MediaType {
        String TEXT = "text";
        String LOCATION = "location";
    }

    public static class Indices extends ObjectCursor.CursorIndices<Message> {
        private final int message_id, created_at, text_content, outgoing, latitude, longitude,
                sender, circle, recipient_id, recipient_type;

        public Indices(@NonNull final Cursor cursor) {
            super(cursor);
            message_id = cursor.getColumnIndex(Messages.MESSAGE_ID);
            created_at = cursor.getColumnIndex(Messages.CREATED_AT);
            text_content = cursor.getColumnIndex(Messages.TEXT_CONTENT);
            outgoing = cursor.getColumnIndex(Messages.OUTGOING);
            latitude = cursor.getColumnIndex(Messages.LATITUDE);
            longitude = cursor.getColumnIndex(Messages.LONGITUDE);
            sender = cursor.getColumnIndex(Messages.SENDER);
            circle = cursor.getColumnIndex(Messages.CIRCLE);
            recipient_id = cursor.getColumnIndex(Messages.RECIPIENT_ID);
            recipient_type = cursor.getColumnIndex(Messages.RECIPIENT_TYPE);
        }

        @Override
        public Message newObject(final Cursor cursor) {
            final Message message = new Message();
            message.setId(cursor.getString(message_id));
            message.setCreatedAt(new Date(cursor.getLong(created_at)));
            message.setTextContent(cursor.getString(text_content));
            message.setOutgoing(cursor.getShort(outgoing) == 1);
            message.setLatitude(cursor.isNull(latitude) ? Double.NaN : cursor.getDouble(latitude));
            message.setLongitude(cursor.isNull(longitude) ? Double.NaN : cursor.getDouble(longitude));
            message.setSender(JsonSerializer.parse(cursor.getString(sender), User.class));
            message.setCircle(JsonSerializer.parse(cursor.getString(circle), Circle.class));
            message.setRecipientId(cursor.getString(recipient_id));
            message.setRecipientType(cursor.getString(recipient_type));
            return message;
        }
    }

}
