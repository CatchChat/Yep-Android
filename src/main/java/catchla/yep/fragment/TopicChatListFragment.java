package catchla.yep.fragment;

import android.accounts.Account;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;

import java.util.List;

import catchla.yep.loader.HistoricalMessagesLoader;
import catchla.yep.model.Message;
import catchla.yep.model.Paging;
import catchla.yep.model.Topic;
import catchla.yep.util.YepAPI;

/**
 * Created by mariotaku on 15/12/10.
 */
public class TopicChatListFragment extends ChatListFragment {
    @Override
    public void onLoadFinished(final Loader<List<Message>> loader, final List<Message> data) {
        super.onLoadFinished(loader, data);
        setRefreshEnabled(false);
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRefreshEnabled(false);
    }

    @Override
    public Loader<List<Message>> onCreateLoader(final int id, final Bundle args) {
        final Topic topic = getTopic();
        final String recipientId = topic.getCircle().getId();
        return new HistoricalMessagesLoader(getContext(), getAccount(),
                YepAPI.PathRecipientType.CIRCLES, recipientId, new Paging(), false, false,
                getAdapter().getData());
    }

    public Topic getTopic() {
        return getArguments().getParcelable(EXTRA_TOPIC);
    }

    public Account getAccount() {
        return getArguments().getParcelable(EXTRA_ACCOUNT);
    }
}
