package catchla.yep.model;

import android.content.Context;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import catchla.yep.R;
import io.realm.RealmObject;

/**
 * Created by mariotaku on 15/6/2.
 */
@JsonObject
public class Provider extends RealmObject {

    @JsonField(name = "name")
    private String name;
    @JsonField(name = "supported")
    private boolean supported;

    public Provider() {
    }

    public Provider(final String name, final boolean supported) {
        this.name = name;
        this.supported = supported;
    }

    public static String getProviderName(final Context context, final String name) {
        if ("dribbble".equals(name)) {
            return context.getString(R.string.dribbble);
        } else if ("github".equals(name)) {
            return context.getString(R.string.github);
        }
        return null;
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

    public static int getProviderIcon(final Context context, final String name) {
        if ("dribbble".equals(name)) {
            return R.drawable.ic_provider_dribbble;
        } else if ("github".equals(name)) {
            return R.drawable.ic_provider_github;
        }
        return 0;
    }

    public static int getProviderColor(final Context context, final String name) {
        if ("dribbble".equals(name)) {
            return context.getResources().getColor(R.color.color_dribbble);
        } else if ("github".equals(name)) {
            return context.getResources().getColor(R.color.color_github);
        }
        return 0;
    }
}
