package catchla.yep.fragment;

import android.support.annotation.WorkerThread;

import catchla.yep.model.NewTopic;
import catchla.yep.model.YepException;
import catchla.yep.util.YepAPI;

/**
 * Created by mariotaku on 16/1/3.
 */
public abstract class NewTopicMediaFragment extends BaseFragment {
    public abstract boolean hasMedia();

    public abstract boolean saveDraft();

    @WorkerThread
    public abstract void uploadMedia(final YepAPI yep, final NewTopic newTopic) throws YepException;

    public abstract void clearDraft();
}
