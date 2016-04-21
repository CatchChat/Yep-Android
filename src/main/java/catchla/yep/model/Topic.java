package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringDef;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.OnJsonParseComplete;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableNoThanks;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

import org.mariotaku.commons.logansquare.JsonStringConverter;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import catchla.yep.model.util.MessageAttachmentsConverter;
import catchla.yep.model.util.YepTimestampDateConverter;

/**
 * Created by mariotaku on 15/10/12.
 */
@ParcelablePlease
@JsonObject
public class Topic implements Parcelable {
    @JsonField(name = "id")
    String id;
    @JsonField(name = "allow_comment")
    boolean allowComment;
    @JsonField(name = "body")
    String body;
    @JsonField(name = "message_count")
    int messageCount;
    @JsonField(name = "created_at", typeConverter = YepTimestampDateConverter.class)
    Date createdAt;
    @JsonField(name = "updated_at", typeConverter = YepTimestampDateConverter.class)
    Date updatedAt;
    @JsonField(name = "user")
    User user;
    @JsonField(name = "skill")
    Skill skill;
    @JsonField(name = "circle")
    Circle circle;
    @JsonField(name = "kind")
    String kind;
    List<Attachment> attachments;

    @JsonField(name = "attachments", typeConverter = JsonStringConverter.class)
    @ParcelableNoThanks
    String attachmentsJson;

    public Topic() {

    }

    public static final Creator<Topic> CREATOR = new Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel in) {
            final Topic topic = new Topic();
            TopicParcelablePlease.readFromParcel(topic, in);
            return topic;
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public boolean isAllowComment() {
        return allowComment;
    }

    public void setAllowComment(final boolean allowComment) {
        this.allowComment = allowComment;
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(final int messageCount) {
        this.messageCount = messageCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(final Skill skill) {
        this.skill = skill;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(final Circle circle) {
        this.circle = circle;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getKind() {
        if (kind == null) return getAttachmentKind();
        return kind;
    }

    public String getAttachmentKind() {
        if (attachments == null) return null;
        //noinspection LoopStatementThatDoesntLoop
        for (final Attachment attachment : attachments) {
            return attachment.getKind();
        }
        return null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Topic topic = (Topic) o;

        return !(id != null ? !id.equals(topic.id) : topic.id != null);

    }

    @Override
    public String toString() {
        return "Topic{" +
                "id='" + id + '\'' +
                ", allowComment=" + allowComment +
                ", body='" + body + '\'' +
                ", messageCount=" + messageCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", user=" + user +
                ", skill=" + skill +
                ", circle=" + circle +
                ", attachments=" + attachments +
                '}';
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        TopicParcelablePlease.writeToParcel(this, dest, flags);
    }

    @OnJsonParseComplete
    void onJsonParseComplete() throws IOException {
        if (kind == null || attachmentsJson == null) return;
        JsonMapper<? extends Attachment> mapper = MessageAttachmentsConverter.getMapperForKind(kind);
        //noinspection unchecked
        attachments = (List<Attachment>) mapper.parseList(attachmentsJson);
    }

    @StringDef({SortOrder.DISTANCE, SortOrder.TIME})
    public @interface SortOrder {
        String DISTANCE = "distance";
        String TIME = "time";
    }

    @StringDef({Kind.GITHUB, Kind.DRIBBBLE, Kind.LOCATION, Kind.IMAGE, Kind.TEXT})
    public @interface Kind {
        String GITHUB = "github";
        String DRIBBBLE = "dribbble";
        String LOCATION = "location";
        String IMAGE = "image";
        String TEXT = "text";
    }

    public static class Converter implements TypeConverter<Topic> {
        @Override
        public Topic parse(final JsonParser jsonParser) throws IOException {
            return LoganSquare.mapperFor(Topic.class).parse(jsonParser);
        }

        @Override
        public void serialize(final Topic object, final String fieldName, final boolean writeFieldNameForObject, final JsonGenerator jsonGenerator) throws IOException {
            if (writeFieldNameForObject) {
                jsonGenerator.writeFieldName(fieldName);
            }
            if (object == null) {
                jsonGenerator.writeNull();
            } else {
                LoganSquare.mapperFor(Topic.class).serialize(object, jsonGenerator, true);
            }
        }
    }
}
