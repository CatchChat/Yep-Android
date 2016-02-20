package catchla.yep.fragment;

import android.os.Bundle;
import android.support.v4.content.Loader;

import java.util.List;

import catchla.yep.loader.MessagesLoader;
import catchla.yep.model.Conversation;
import catchla.yep.model.Message;

/**
 * Created by mariotaku on 15/12/10.
 */
public class ConversationChatListFragment extends ChatListFragment {
    @Override
    public Loader<List<Message>> onCreateLoader(final int id, final Bundle args) {
        return new MessagesLoader(getContext(), getConversation());
    }

    public Conversation getConversation() {
        return getArguments().getParcelable(EXTRA_CONVERSATION);
    }
}
