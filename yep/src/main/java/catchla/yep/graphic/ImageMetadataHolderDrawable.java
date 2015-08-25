package catchla.yep.graphic;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;

import catchla.yep.model.Message;

/**
 * Created by mariotaku on 15/8/25.
 */
public class ImageMetadataHolderDrawable extends BitmapDrawable {
    public ImageMetadataHolderDrawable(final Resources resources, final Message.Attachment.ImageMetadata metadata) {
        super(resources, getMetadataBitmap(metadata));
    }

    private static Bitmap getMetadataBitmap(final Message.Attachment.ImageMetadata metadata) {
        final byte[] bytes = Base64.decode(metadata.getBlurredThumbnail(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
