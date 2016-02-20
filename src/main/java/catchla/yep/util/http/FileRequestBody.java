package catchla.yep.util.http;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by mariotaku on 15/12/1.
 */
public class FileRequestBody extends RequestBody implements Closeable {

    private final Source source;
    private final MediaType contentType;
    private final long contentLength;
    private final String fileName;

    public FileRequestBody(Source source, String fileName, MediaType contentType, long contentLength) {
        this.source = source;
        this.fileName = fileName;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public String getFileName() {
        return fileName;
    }

    public static FileRequestBody create(File file) throws FileNotFoundException {
        return new FileRequestBody(Okio.source(file), file.getName(),
                MediaType.parse("application/octet-stream"), file.length());
    }

    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public long contentLength() throws IOException {
        return contentLength;
    }

    @Override
    public void writeTo(final BufferedSink sink) throws IOException {
        sink.writeAll(source);
    }

    @Override
    public void close() throws IOException {
        source.close();
    }
}
