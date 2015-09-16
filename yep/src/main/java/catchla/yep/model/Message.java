package catchla.yep.model;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import catchla.yep.Constants;
import catchla.yep.model.util.NaNDoubleConverter;
import catchla.yep.model.util.YepTimestampDateConverter;
import catchla.yep.provider.YepDataStore.Messages;
import catchla.yep.util.JsonSerializer;
import catchla.yep.util.Utils;

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
    String id;
    @JsonField(name = "recipient_id")
    String recipientId;
    @JsonField(name = "parent_id")
    String parentId;
    @JsonField(name = "text_content")
    String textContent;
    @JsonField(name = "created_at", typeConverter = YepTimestampDateConverter.class)
    Date createdAt;
    @JsonField(name = "sender")
    User sender;
    @JsonField(name = "recipient_type")
    String recipientType;
    @JsonField(name = "media_type")
    String mediaType;
    @JsonField(name = "circle")
    Circle circle;
    @JsonField(name = "conversation_id")
    String conversationId;
    @JsonField(name = "outgoing")
    boolean outgoing;
    @JsonField(name = "state")
    String state;
    @JsonField(name = "attachments")
    List<Attachment> attachments;
    @JsonField(name = "local_metadata")
    List<LocalMetadata> localMetadata;

    public List<LocalMetadata> getLocalMetadata() {
        return localMetadata;
    }

    public void setLocalMetadata(final List<LocalMetadata> localMetadata) {
        this.localMetadata = localMetadata;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<Attachment> attachments) {
        this.attachments = attachments;
    }

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

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public interface RecipientType {
        String USER = "User";
        String CIRCLE = "Circle";
    }

    public interface MediaType {
        String TEXT = "text";
        String LOCATION = "location";
        String IMAGE = "image";
        String AUDIO = "audio";
    }


    @JsonObject
    public static class Attachment {
        @JsonField(name = "kind")
        String kind;
        @JsonField(name = "metadata")
        String metadata;
        @JsonField(name = "file")
        File file;

        public File getFile() {
            return file;
        }

        public String getKind() {
            return kind;
        }

        public String getMetadata() {
            return metadata;
        }

        public interface Metadata {

        }

        @JsonObject
        public static class File {
            @JsonField(name = "storage")
            String storage;
            @JsonField(name = "expires_in")
            String expiresIn;
            @JsonField(name = "url")
            String url;

            public String getStorage() {
                return storage;
            }

            public String getExpiresIn() {
                return expiresIn;
            }

            public String getUrl() {
                return url;
            }
        }

        @JsonObject
        public static class AudioMetadata implements Metadata {
            @JsonField(name = "audio_samples")
            float[] samples;
            @JsonField(name = "audio_duration")
            float duration;

            public float getDuration() {
                return duration;
            }

            public void setDuration(final float duration) {
                this.duration = duration;
            }

            public float[] getSamples() {
                return samples;
            }

            public void setSamples(final float[] samples) {
                this.samples = samples;
            }
        }

        @JsonObject
        public static class ImageMetadata implements Metadata {
            @JsonField(name = "image_width")
            int width;
            @JsonField(name = "image_height")
            int height;
            @JsonField(name = "blurred_thumbnail_string")
            String blurredThumbnail;

            public int getWidth() {
                return width;
            }

            public void setWidth(final int width) {
                this.width = width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(final int height) {
                this.height = height;
            }

            public String getBlurredThumbnail() {
                return blurredThumbnail;
            }

            public void setBlurredThumbnail(final String blurredThumbnail) {
                this.blurredThumbnail = blurredThumbnail;
            }

            public static ImageMetadata getImageMetadata(final String imagePath) {
                final BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imagePath, o);
                final ImageMetadata metadata = new ImageMetadata();
                boolean swapWH = false;
                try {
                    ExifInterface exif = new ExifInterface(imagePath);
                    final int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    swapWH = orientation == ExifInterface.ORIENTATION_ROTATE_270 || orientation == ExifInterface.ORIENTATION_ROTATE_90;
                } catch (IOException ignore) {

                }
                metadata.setWidth(swapWH ? o.outHeight : o.outWidth);
                metadata.setHeight(swapWH ? o.outWidth : o.outHeight);
                o.inJustDecodeBounds = false;
                o.inSampleSize = Math.max(1, Math.max(o.outWidth, o.outHeight) / 100);
                final Bitmap downScaledBitmap = BitmapFactory.decodeFile(imagePath, o);
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final Base64OutputStream os = new Base64OutputStream(baos, Base64.URL_SAFE);
                try {
                    downScaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, os);
                    os.flush();
                    metadata.setBlurredThumbnail(baos.toString("ASCII"));
                } catch (IOException e) {
                    Log.w(Constants.LOGTAG, e);
                } finally {
                    downScaledBitmap.recycle();
                    Utils.closeSilently(os);
                }
                return metadata;
            }
        }
    }

    public static class Indices extends ObjectCursor.CursorIndices<Message> {
        private final int message_id, created_at, text_content, outgoing, latitude, longitude,
                sender, circle, recipient_id, recipient_type, media_type, state, attachments,
                conversation_id, local_metadata;

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
            media_type = cursor.getColumnIndex(Messages.MEDIA_TYPE);
            state = cursor.getColumnIndex(Messages.STATE);
            attachments = cursor.getColumnIndex(Messages.ATTACHMENTS);
            conversation_id = cursor.getColumnIndex(Messages.CONVERSATION_ID);
            local_metadata = cursor.getColumnIndex(Messages.LOCAL_METADATA);
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
            message.setMediaType(cursor.getString(media_type));
            message.setState(cursor.getString(state));
            message.setAttachments(JsonSerializer.parseList(cursor.getString(attachments), Attachment.class));
            message.setLocalMetadata(JsonSerializer.parseList(cursor.getString(local_metadata), LocalMetadata.class));
            message.setConversationId(cursor.getString(conversation_id));
            return message;
        }
    }

    @JsonObject
    public static class LocalMetadata {
        @JsonField(name = "name")
        String name;
        @JsonField(name = "value")
        String value;

        public LocalMetadata() {
        }

        public LocalMetadata(final String name, final String value) {
            this.name = name;
            this.value = value;
        }

        public static String get(final NewMessage message, final String key) {
            return get(message.localMetadata(), key);
        }

        public static String get(final LocalMetadata[] metadata, final String key) {
            if (metadata == null) return null;
            for (LocalMetadata item : metadata) {
                if (item.name.equals(key)) return item.value;
            }
            return null;
        }

        public static String get(final List<LocalMetadata> metadata, final String key) {
            if (metadata == null) return null;
            for (LocalMetadata item : metadata) {
                if (item.name.equals(key)) return item.value;
            }
            return null;
        }
    }
}
