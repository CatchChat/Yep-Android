package catchla.yep.model;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.IOException;

import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.http.FileRequestBody;

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

    public static class Converter implements retrofit.Converter<AttachmentUpload, RequestBody> {
        @Override
        public RequestBody convert(final AttachmentUpload value) throws IOException {
            return value.toRequestBody();
        }
    }

    private RequestBody toRequestBody() {
        final MultipartBuilder builder = new MultipartBuilder();
        builder.addFormDataPart("file", Utils.getFilename(file, mimeType),
                FileRequestBody.create(MediaType.parse(mimeType), file));
        builder.addFormDataPart("attachable_type", kind);
        builder.addFormDataPart("metadata", metadata);
        return builder.build();
    }

}
