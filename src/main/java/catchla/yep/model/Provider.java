package catchla.yep.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

import catchla.yep.R;

/**
 * Created by mariotaku on 15/6/2.
 */
@ParcelablePlease
@JsonObject
public class Provider implements Parcelable {

    public static final String PROVIDER_INSTAGRAM = "instagram";
    public static final String PROVIDER_GITHUB = "github";
    public static final String PROVIDER_DRIBBBLE = "dribbble";
    public static final String PROVIDER_BLOG = "blog";

    public static final Creator<Provider> CREATOR = new Creator<Provider>() {
        @Override
        public Provider createFromParcel(Parcel in) {
            return new Provider(in);
        }

        @Override
        public Provider[] newArray(int size) {
            return new Provider[size];
        }
    };
    @ParcelableThisPlease
    @JsonField(name = "name")
    String name;
    @ParcelableThisPlease
    @JsonField(name = "supported")
    boolean supported;

    public Provider() {
    }

    public Provider(Parcel src) {
        ProviderParcelablePlease.readFromParcel(this, src);
    }

    public Provider(final String name, final boolean supported) {
        this.name = name;
        this.supported = supported;
    }

    public static String getProviderName(final Context context, final String name) {
        switch (name) {
            case PROVIDER_DRIBBBLE:
                return context.getString(R.string.dribbble);
            case PROVIDER_GITHUB:
                return context.getString(R.string.github);
            case PROVIDER_INSTAGRAM:
                return context.getString(R.string.instagram);
            case PROVIDER_BLOG:
                return context.getString(R.string.blog);
        }
        return null;
    }

    public static int getProviderIcon(final Context context, final String name) {
        if (PROVIDER_DRIBBBLE.equals(name)) {
            return R.drawable.ic_provider_dribbble;
        } else if (PROVIDER_GITHUB.equals(name)) {
            return R.drawable.ic_provider_github;
        } else if (PROVIDER_INSTAGRAM.equals(name)) {
            return R.drawable.ic_provider_instagram;
        } else if (PROVIDER_BLOG.equals(name)) {
            return R.drawable.ic_provider_web;
        }
        return 0;
    }

    public static int getProviderColor(final Context context, final String name) {
        if (PROVIDER_DRIBBBLE.equals(name)) {
            return ContextCompat.getColor(context, R.color.color_dribbble);
        } else if (PROVIDER_GITHUB.equals(name)) {
            return ContextCompat.getColor(context, R.color.color_github);
        } else if (PROVIDER_INSTAGRAM.equals(name)) {
            return ContextCompat.getColor(context, R.color.color_instagram);
        } else if (PROVIDER_BLOG.equals(name)) {
            return ContextCompat.getColor(context, R.color.color_blog);
        }
        return 0;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isSupported() {
        return supported;
    }

    public void setSupported(final boolean supported) {
        this.supported = supported;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        ProviderParcelablePlease.writeToParcel(this, dest, flags);
    }

}
