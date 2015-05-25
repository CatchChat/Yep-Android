package catchla.yep.util.net;

import android.support.annotation.Nullable;

import org.json.JSONObject;
import org.mariotaku.restfu.http.ContentType;
import org.mariotaku.restfu.http.mime.StringTypedData;

import java.nio.charset.Charset;

/**
 * Created by mariotaku on 15/5/25.
 */
public class JSONTypedData extends StringTypedData {

    private static final ContentType CONTENT_TYPE = ContentType.parse("application/json");

    JSONTypedData(JSONObject json) {
        super(json.toString(), Charset.defaultCharset());
    }

    @Nullable
    @Override
    public ContentType contentType() {
        return CONTENT_TYPE;
    }

}

