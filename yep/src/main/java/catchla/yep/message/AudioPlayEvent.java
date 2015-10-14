package catchla.yep.message;

import catchla.yep.model.Attachment;

/**
 * Created by mariotaku on 15/10/9.
 */
public class AudioPlayEvent {
    public static final int START = 1;
    public static final int END = 2;
    public static final int PROGRESS = 3;

    public final int what;
    public final Attachment attachment;
    public final float progress;

    public AudioPlayEvent(final int what, final Attachment attachment, final float progress) {
        this.what = what;
        this.attachment = attachment;
        this.progress = progress;
    }

    public static AudioPlayEvent start(final Attachment attachment) {
        return new AudioPlayEvent(START, attachment, 0);
    }

    public static AudioPlayEvent end(final Attachment attachment) {
        return new AudioPlayEvent(END, attachment, 1);
    }

    public static AudioPlayEvent progress(final Attachment attachment, float progress) {
        return new AudioPlayEvent(PROGRESS, attachment, progress);
    }
}
