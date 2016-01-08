package catchla.yep.fragment;

import catchla.yep.model.LocationAttachment;
import catchla.yep.model.NewTopic;
import catchla.yep.model.Topic;
import catchla.yep.model.YepException;
import catchla.yep.util.YepAPI;

/**
 * Created by mariotaku on 16/1/6.
 */
public class NewTopicLocationFragment extends NewTopicMediaFragment {
    @Override
    public boolean hasMedia() {
        return getAttachment() != null;
    }

    @Override
    public boolean saveDraft() {
        return false;
    }

    @Override
    public void uploadMedia(final YepAPI yep, final NewTopic newTopic) throws YepException {
        newTopic.kind(Topic.Kind.LOCATION);
        newTopic.attachments(getAttachment());
    }

    public LocationAttachment getAttachment() {
        return getArguments().getParcelable(EXTRA_ATTACHMENT);
    }

    @Override
    public void clearDraft() {

    }
}
