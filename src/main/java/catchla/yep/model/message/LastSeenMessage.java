package catchla.yep.model.message;

import android.text.TextUtils;

import catchla.yep.model.Conversation;

/**
 * Created by mariotaku on 16/4/21.
 */
public class LastSeenMessage {
    private final String mAccountId;
    private final String mConversationId;
    private final long mLastSeen;

    public LastSeenMessage(final String accountId, final String conversationId, final long lastSeen) {
        mAccountId = accountId;
        mConversationId = conversationId;
        mLastSeen = lastSeen;
    }

    public String getAccountId() {
        return mAccountId;
    }

    public String getConversationId() {
        return mConversationId;
    }

    public long getLastSeen() {
        return mLastSeen;
    }

    public boolean isConversation(final Conversation conversation) {
        return TextUtils.equals(mAccountId, conversation.getAccountId())
                && TextUtils.equals(mConversationId, conversation.getId());
    }
}
