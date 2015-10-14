package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import catchla.yep.util.JsonSerializer;

/**
 * Created by mariotaku on 15/10/14.
 */
@JsonObject
public class NewAttachmentFile {
    @JsonField(name = "file")
    String file;
    @JsonField(name = "metadata")
    String metadata;

    public NewAttachmentFile() {
    }

    public NewAttachmentFile(final S3UploadToken file, final Attachment.Metadata metadata) {
        this(file.getOptions().getKey(), metadata);
    }

    public NewAttachmentFile(final S3UploadToken file, final String metadata) {
        this(file.getOptions().getKey(), metadata);
    }

    public NewAttachmentFile(final String file, final Attachment.Metadata metadata) {
        setFile(file);
        setMetadata(metadata);
    }

    public NewAttachmentFile(final String file, final String metadata) {
        setFile(file);
        setMetadata(metadata);
    }

    public void setFile(final String file) {
        this.file = file;
    }

    public void setMetadata(final Attachment.Metadata metadata) {
        setMetadata(JsonSerializer.serialize(metadata));
    }

    public void setMetadata(final String metadata) {
        this.metadata = metadata;
    }
}
