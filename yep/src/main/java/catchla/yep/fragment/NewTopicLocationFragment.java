package catchla.yep.fragment;

import catchla.yep.model.LocationAttachment;
import catchla.yep.model.NewTopic;
import catchla.yep.model.YepException;
import catchla.yep.util.YepAPI;

/**
 * Created by mariotaku on 16/1/6.
 */
public class NewTopicLocationFragment extends NewTopicMediaFragment {
    @Override
    public boolean hasMedia() {
        return true;
    }

    @Override
    public boolean saveDraft() {
        return false;
    }

    @Override
    public void uploadMedia(final YepAPI yep, final NewTopic newTopic) throws YepException {
        LocationAttachment attachment = new LocationAttachment();
        newTopic.attachments(attachment);
    }

    @Override
    public void clearDraft() {

    }
}
