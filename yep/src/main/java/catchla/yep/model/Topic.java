package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringDef;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

import java.util.Date;
import java.util.List;

import catchla.yep.model.util.VariableTypeAttachmentsConverter;
import catchla.yep.model.util.YepTimestampDateConverter;

/**
 * Created by mariotaku on 15/10/12.
 */
@ParcelablePlease
@JsonObject
public class Topic implements Parcelable {
    @ParcelableThisPlease
    @JsonField(name = "id")
    String id;
    @ParcelableThisPlease
    @JsonField(name = "allow_comment")
    boolean allowComment;
    @ParcelableThisPlease
    @JsonField(name = "body")
    String body;
    @ParcelableThisPlease
    @JsonField(name = "message_count")
    int messageCount;
    @ParcelableThisPlease
    @JsonField(name = "created_at", typeConverter = YepTimestampDateConverter.class)
    Date createdAt;
    @ParcelableThisPlease
    @JsonField(name = "updated_at", typeConverter = YepTimestampDateConverter.class)
    Date updatedAt;
    @ParcelableThisPlease
    @JsonField(name = "user")
    User user;
    @ParcelableThisPlease
    @JsonField(name = "skill")
    Skill skill;
    @ParcelableThisPlease
    @JsonField(name = "circle")
    Circle circle;
    @ParcelableThisPlease
    @JsonField(name = "attachments", typeConverter = VariableTypeAttachmentsConverter.class)
    List<Attachment> attachments;

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

    public Topic() {

    }

    protected Topic(Parcel in) {
        TopicParcelablePlease.readFromParcel(this, in);
    }

    public static final Creator<Topic> CREATOR = new Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Topic topic = (Topic) o;

        return !(id != null ? !id.equals(topic.id) : topic.id != null);

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

    public String getAttachmentKind() {
        if (attachments == null) return null;
        //noinspection LoopStatementThatDoesntLoop
        for (final Attachment attachment : attachments) {
            return attachment.getKind();
        }
        return null;
    }

    @StringDef({SortOrder.DISTANCE, SortOrder.TIME})
    public @interface SortOrder {
        String DISTANCE = "distance";
        String TIME = "time";
    }
}
