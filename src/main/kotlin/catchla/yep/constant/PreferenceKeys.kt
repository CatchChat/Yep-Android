package catchla.yep.constant

import android.content.SharedPreferences
import catchla.yep.model.DiscoverSortOrder
import catchla.yep.model.TopicDraft
import catchla.yep.model.TopicSortOrder
import org.mariotaku.kpreferences.KPreferenceKey
import org.mariotaku.kpreferences.KStringKey

/**
 * Created by mariotaku on 16/8/25.
 */

val topicsSortOrderKey = KStringKey("topics_sort_order", TopicSortOrder.DEFAULT)
val discoverSortOrderKey = KStringKey("discover_sort_order", DiscoverSortOrder.SCORE)

object topicDraftKey : KPreferenceKey<TopicDraft?> {
    val textKey = "topic_draft_text"
    val kindKey = "topic_draft_kind"
    val attachmentKey = "topic_draft_attachment"

    override fun contains(preferences: SharedPreferences): Boolean {
        return preferences.contains(textKey)
    }

    override fun read(preferences: SharedPreferences): TopicDraft? {
        val text = preferences.getString(textKey, null) ?: return null
        val kind = preferences.getString(kindKey, null)
        val attachment = preferences.getString(attachmentKey, null)
        return TopicDraft(text, kind, attachment)
    }

    override fun write(editor: SharedPreferences.Editor, value: TopicDraft?): Boolean {
        if (value != null) {
            editor.putString(textKey, value.text)
            editor.putString(kindKey, value.kind)
        } else {
            editor.remove(textKey)
            editor.remove(kindKey)
        }
        return true
    }

}
