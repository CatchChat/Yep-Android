package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by mariotaku on 15/10/14.
 */
@JsonObject
public class NewAudioAttachment implements NewAttachment {

    @JsonField(name = "audio")
    NewAttachmentFile[] audio;

    public NewAudioAttachment() {

    }

    public NewAudioAttachment(final NewAttachmentFile... audio) {
        this.audio = audio;
    }

    public NewAudioAttachment(S3UploadToken token, String metadata) {
        audio = new NewAttachmentFile[]{new NewAttachmentFile(token.getOptions().getKey(), metadata)};
    }

    public NewAudioAttachment(S3UploadToken token, FileAttachment.AudioMetadata metadata) {
        audio = new NewAttachmentFile[]{new NewAttachmentFile(token.getOptions().getKey(), metadata)};
    }

}
