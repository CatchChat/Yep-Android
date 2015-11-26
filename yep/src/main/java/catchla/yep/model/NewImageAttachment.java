package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * Created by mariotaku on 15/10/14.
 */
@JsonObject
public class NewImageAttachment implements NewAttachment {

    @JsonField(name = "image")
    NewAttachmentFile[] image;

    public NewImageAttachment() {

    }

    public NewImageAttachment(final NewAttachmentFile... image) {
        this.image = image;
    }

    public NewImageAttachment(S3UploadToken token, BasicAttachment.ImageMetadata metadata) {
        image = new NewAttachmentFile[]{new NewAttachmentFile(token.getOptions().getKey(), metadata)};
    }

    public NewImageAttachment(S3UploadToken token, String metadata) {
        image = new NewAttachmentFile[]{new NewAttachmentFile(token.getOptions().getKey(), metadata)};
    }

    public NewImageAttachment(final List<NewAttachmentFile> image) {
        this(image.toArray(new NewAttachmentFile[image.size()]));
    }
}
