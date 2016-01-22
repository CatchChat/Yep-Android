package catchla.yep.model;

import org.mariotaku.restfu.RestConverter;
import org.mariotaku.restfu.http.ContentType;
import org.mariotaku.restfu.http.mime.Body;
import org.mariotaku.restfu.http.mime.FileBody;
import org.mariotaku.restfu.http.mime.MultipartBody;
import org.mariotaku.restfu.http.mime.StringBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;

/**
 * Created by mariotaku on 15/12/27.
 */
public class AttachmentUpload {
    private final File file;
    private final String mimeType;
    private final String kind;
    private final String metadata;

    public AttachmentUpload(final File file, final String mimeType, final String kind, final String metadata) {
        this.file = file;
        this.mimeType = mimeType;
        this.kind = kind;
        this.metadata = metadata;
    }

    public static AttachmentUpload create(final File file, final String mimeType,
                                          @YepAPI.AttachableType final String kind,
                                          final String metadata) {
        return new AttachmentUpload(file, mimeType, kind, metadata);
    }

    public static class Converter implements RestConverter<AttachmentUpload, Body, YepException> {

        @Override
        public Body convert(final AttachmentUpload from) throws ConvertException, IOException, YepException {
            return from.toRequestBody();
        }
    }

    private MultipartBody toRequestBody() throws FileNotFoundException {
        final MultipartBody body = new MultipartBody();
        body.add("file", new FileBody(new FileInputStream(file), Utils.getFilename(file, mimeType),
                file.length(), ContentType.parse(mimeType)));
        body.add("attachable_type", new StringBody(kind, Charset.defaultCharset()));
        body.add("metadata", new StringBody(metadata, Charset.defaultCharset()));
        return body;
    }

}
