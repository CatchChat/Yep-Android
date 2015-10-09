package catchla.yep.message;

import catchla.yep.model.Message;

/**
 * Created by mariotaku on 15/10/9.
 */
public class AudioPlayEvent {
    public static final int START = 1;
    public static final int END = 2;
    public static final int PROGRESS = 3;

    public final int what;
    public final Message.Attachment attachment;
    public final float progress;

    public AudioPlayEvent(final int what, final Message.Attachment attachment, final float progress) {
        this.what = what;
        this.attachment = attachment;
        this.progress = progress;
    }

    public static AudioPlayEvent start(final Message.Attachment attachment) {
        return new AudioPlayEvent(START, attachment, 0);
    }

    public static AudioPlayEvent end(final Message.Attachment attachment) {
        return new AudioPlayEvent(END, attachment, 1);
    }

    public static AudioPlayEvent progress(final Message.Attachment attachment, float progress) {
        return new AudioPlayEvent(PROGRESS, attachment, progress);
    }
}
