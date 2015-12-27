package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

import org.mariotaku.library.objectcursor.annotation.CursorField;
import org.mariotaku.library.objectcursor.annotation.CursorObject;

import java.util.Date;

import catchla.yep.model.util.LoganSquareCursorFieldConverter;
import catchla.yep.model.util.TimestampToDateConverter;
import catchla.yep.model.util.YepTimestampDateConverter;
import catchla.yep.provider.YepDataStore.Circles;

/**
 * Created by mariotaku on 15/5/29.
 */
@ParcelablePlease
@JsonObject
@CursorObject(valuesCreator = true)
public class Circle implements Parcelable {

    @ParcelableThisPlease
    @JsonField(name = "id")
    @CursorField(Circles.CIRCLE_ID)
    String id;
    @ParcelableThisPlease
    @JsonField(name = "name")
    @CursorField(Circles.NAME)
    String name;
    @ParcelableThisPlease
    @JsonField(name = "topic_id")
    @CursorField(Circles.TOPIC_ID)
    String topicId;
    @ParcelableThisPlease
    @JsonField(name = "topic")
    @CursorField(value = Circles.TOPIC, converter = LoganSquareCursorFieldConverter.class)
    Topic topic;

    @ParcelableThisPlease
    @JsonField(name = "kind")
    @CursorField(Circles.KIND)
    String kind;
    @ParcelableThisPlease
    @JsonField(name = "active")
    @CursorField(Circles.ACTIVE)
    boolean active;
    @ParcelableThisPlease
    @JsonField(name = "created_at", typeConverter = YepTimestampDateConverter.class)
    @CursorField(value = Circles.CREATED_AT, converter = TimestampToDateConverter.class)
    Date createdAt;
    @ParcelableThisPlease
    @JsonField(name = "updated_at", typeConverter = YepTimestampDateConverter.class)
    @CursorField(value = Circles.UPDATED_AT, converter = TimestampToDateConverter.class)
    Date updatedAt;

    public Circle() {

    }

    protected Circle(Parcel in) {
        CircleParcelablePlease.readFromParcel(this, in);
    }

    public static final Creator<Circle> CREATOR = new Creator<Circle>() {
        @Override
        public Circle createFromParcel(Parcel in) {
            return new Circle(in);
        }

        @Override
        public Circle[] newArray(int size) {
            return new Circle[size];
        }
    };

    public Topic getTopic() {
        return topic;
    }

    public String getId() {

        return id;
    }

    public String getTopicId() {
        return topicId;
    }

    public String getKind() {
        return kind;
    }

    public boolean isActive() {
        return active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        CircleParcelablePlease.writeToParcel(this, dest, flags);
    }

    @Override
    public String toString() {
        return "Circle{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", topicId='" + topicId + '\'' +
                ", topic=" + topic +
                ", kind='" + kind + '\'' +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public @interface Kind {
        String TOPIC_CIRCLE = "TopicCircle";
    }
}
