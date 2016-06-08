package catchla.yep.model.message

import android.text.TextUtils

import catchla.yep.model.Conversation

/**
 * Created by mariotaku on 16/4/21.
 */
data class LastSeenMessage(
        val accountId: String,
        val conversationId: String,
        val lastSeen: Long
) {

    fun isConversation(conversation: Conversation): Boolean {
        return TextUtils.equals(accountId, conversation.accountId) && TextUtils.equals(conversationId, conversation.id)
    }
}
