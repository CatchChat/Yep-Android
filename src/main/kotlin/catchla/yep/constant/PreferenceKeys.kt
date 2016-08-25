package catchla.yep.constant

import android.content.SharedPreferences
import catchla.yep.model.TopicDraft
import org.mariotaku.kpreferences.KPreferenceKey

/**
 * Created by mariotaku on 16/8/25.
 */
object topicDraftKey : KPreferenceKey<TopicDraft?> {
    val textKey = "topic_drafts_text"
    override fun contains(preferences: SharedPreferences): Boolean {
        return preferences.contains(textKey)
    }

    override fun read(preferences: SharedPreferences): TopicDraft? {
        val text = preferences.getString(textKey, null) ?: return null
        return TopicDraft(text)
    }

    override fun write(editor: SharedPreferences.Editor, value: TopicDraft?): Boolean {
        if (value != null) {
            editor.putString(textKey, value.text)
        } else {
            editor.remove(textKey)
        }
        return true
    }

}
