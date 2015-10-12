package catchla.yep.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

/**
 * Created by mariotaku on 15/5/29.
 */
@ParcelablePlease
@JsonObject
public class Circle implements Parcelable {

    @ParcelableThisPlease
    @JsonField(name = "id")
    String id;
    @ParcelableThisPlease
    @JsonField(name = "name")
    String name;

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

    public String getId() {
        return id;
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
                '}';
    }
}
