package catchla.yep.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.mariotaku.restfu.http.ContentType;
import org.mariotaku.restfu.http.mime.StringTypedData;
import org.mariotaku.restfu.http.mime.TypedData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public abstract class AbsJsonBody implements TypedData {

    private final StringTypedData delegated;

    protected AbsJsonBody(String json) {
        delegated = new StringTypedData(json,
                ContentType.parse("application/json").charset(Charset.defaultCharset()));
    }

    @Override
    @Nullable
    public ContentType contentType() {
        return delegated.contentType();
    }

    @Override
    public String contentEncoding() {
        return delegated.contentEncoding();
    }

    @Override
    public long length() throws IOException {
        return delegated.length();
    }

    @Override
    public long writeTo(@NonNull final OutputStream os) throws IOException {
        return delegated.writeTo(os);
    }

    @Override
    @NonNull
    public InputStream stream() throws IOException {
        return delegated.stream();
    }

    @Override
    public void close() throws IOException {
        delegated.close();
    }

}