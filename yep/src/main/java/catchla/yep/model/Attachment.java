package catchla.yep.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import catchla.yep.Constants;
import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 15/10/14.
 */
@ParcelablePlease
@JsonObject
public class Attachment implements Parcelable {
    public static final Creator<Attachment> CREATOR = new Creator<Attachment>() {
        @Override
        public Attachment createFromParcel(Parcel in) {
            return new Attachment(in);
        }

        @Override
        public Attachment[] newArray(int size) {
            return new Attachment[size];
        }
    };
    @ParcelableThisPlease
    @JsonField(name = "kind")
    String kind;
    @ParcelableThisPlease
    @JsonField(name = "metadata")
    String metadata;
    @ParcelableThisPlease
    @JsonField(name = "file")
    AttachmentFile file;

    protected Attachment(Parcel in) {
        AttachmentParcelablePlease.readFromParcel(this, in);
    }

    public Attachment() {

    }

    public AttachmentFile getFile() {
        return file;
    }

    public String getKind() {
        return kind;
    }

    public String getMetadata() {
        return metadata;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        AttachmentParcelablePlease.writeToParcel(this, dest, flags);
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "kind='" + kind + '\'' +
                ", metadata='" + metadata + '\'' +
                ", file=" + file +
                '}';
    }

    public interface Metadata {

    }

    @JsonObject
    public static class AudioMetadata implements Metadata {
        @JsonField(name = "audio_samples")
        float[] samples;
        @JsonField(name = "audio_duration")
        float duration;

        public float getDuration() {
            return duration;
        }

        public void setDuration(final float duration) {
            this.duration = duration;
        }

        public float[] getSamples() {
            return samples;
        }

        public void setSamples(final float[] samples) {
            this.samples = samples;
        }
    }

    @JsonObject
    public static class ImageMetadata implements Metadata {
        @JsonField(name = "image_width")
        int width;
        @JsonField(name = "image_height")
        int height;
        @JsonField(name = "blurred_thumbnail_string")
        String blurredThumbnail;

        public static ImageMetadata getImageMetadata(final String imagePath) {
            final BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, o);
            final ImageMetadata metadata = new ImageMetadata();
            boolean swapWH = false;
            try {
                ExifInterface exif = new ExifInterface(imagePath);
                final int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                swapWH = orientation == ExifInterface.ORIENTATION_ROTATE_270 || orientation == ExifInterface.ORIENTATION_ROTATE_90;
            } catch (IOException ignore) {

            }
            metadata.setWidth(swapWH ? o.outHeight : o.outWidth);
            metadata.setHeight(swapWH ? o.outWidth : o.outHeight);
            o.inJustDecodeBounds = false;
            o.inSampleSize = Math.max(1, Math.max(o.outWidth, o.outHeight) / 100);
            final Bitmap downScaledBitmap = BitmapFactory.decodeFile(imagePath, o);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final Base64OutputStream os = new Base64OutputStream(baos, Base64.URL_SAFE);
            try {
                downScaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, os);
                os.flush();
                metadata.setBlurredThumbnail(baos.toString("ASCII"));
            } catch (IOException e) {
                Log.w(Constants.LOGTAG, e);
            } finally {
                downScaledBitmap.recycle();
                Utils.closeSilently(os);
            }
            return metadata;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(final int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(final int height) {
            this.height = height;
        }

        public String getBlurredThumbnail() {
            return blurredThumbnail;
        }

        public void setBlurredThumbnail(final String blurredThumbnail) {
            this.blurredThumbnail = blurredThumbnail;
        }
    }
}
