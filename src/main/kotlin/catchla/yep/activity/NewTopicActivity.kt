package catchla.yep.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.extension.account
import catchla.yep.fragment.NewTopicGalleryFragment
import catchla.yep.fragment.NewTopicLocationFragment
import catchla.yep.fragment.NewTopicMediaFragment
import catchla.yep.fragment.ProgressDialogFragment
import catchla.yep.model.NewTopic
import catchla.yep.model.Skill
import catchla.yep.util.Utils
import catchla.yep.util.YepAPIFactory
import kotlinx.android.synthetic.main.activity_new_topic.*
import nl.komponents.kovenant.task
import nl.komponents.kovenant.ui.failUi
import nl.komponents.kovenant.ui.successUi
import org.apache.commons.lang3.StringUtils

/**
 * Created by mariotaku on 15/10/13.
 */
class NewTopicActivity : SwipeBackContentActivity(), Constants {
    private var mDismissUploadingDialogRunnable: Runnable? = null

    private lateinit var preferences: SharedPreferences

    private var draftsSaved: Boolean = false
    private var shouldSkipSaveDrafts: Boolean = false
    private var fragmentResumed: Boolean = false

    override fun onPause() {
        fragmentResumed = false
        super.onPause()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        fragmentResumed = true
        invokeFragmentRunnable()
    }

    private fun invokeFragmentRunnable() {
        if (fragmentResumed && mDismissUploadingDialogRunnable != null) {
            mDismissUploadingDialogRunnable!!.run()
        }
    }

    override fun onContentChanged() {
        super.onContentChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        setContentView(R.layout.activity_new_topic)

        if (savedInstanceState == null) {
            editText.setText(preferences.getString(KEY_TOPIC_DRAFTS_TEXT, null))
            editText.setSelection(editText.length())
        }
        val intent = intent
        val skill = intent.getParcelableExtra<Skill>(Constants.EXTRA_SKILL)
        var newTopicsType: String? = intent.getStringExtra(Constants.EXTRA_NEW_TOPIC_TYPE)
        if (newTopicsType == null) {
            newTopicsType = TYPE_PHOTOS_TEXT
        }
        when (newTopicsType) {
            TYPE_PHOTOS_TEXT -> {
                val fm = supportFragmentManager
                val ft = fm.beginTransaction()
                ft.replace(R.id.new_topic_media, NewTopicGalleryFragment())
                ft.commit()
            }
            TYPE_AUDIO -> {
            }
            TYPE_LOCATION -> {
                val fm = supportFragmentManager
                val ft = fm.beginTransaction()
                val fragment = NewTopicLocationFragment()
                val args = Bundle()
                args.putParcelable(Constants.EXTRA_ATTACHMENT, intent.getParcelableExtra<Parcelable>(Constants.EXTRA_ATTACHMENT))
                fragment.arguments = args
                ft.replace(R.id.new_topic_media, fragment)
                ft.commit()
            }
            else -> {
                throw UnsupportedOperationException(newTopicsType)
            }
        }
        val adapter = SkillSpinnerAdapter(this)
        val accountUser = Utils.getAccountUser(this, account)
        adapter.add(Skill.getDummy())
        if (skill != null) {
            adapter.add(skill)
        }
        adapter.addAll(accountUser.masterSkills ?: emptyList())
        adapter.addAll(accountUser.learningSkills ?: emptyList())
        topicSpinner.adapter = adapter
        if (skill != null) {
            topicSpinner.setSelection(adapter.findPositionBySkillId(skill.id))
        }
    }

    override fun onStop() {
        draftsSaved = saveDrafts()
        super.onStop()
    }

    private fun saveDrafts(): Boolean {
        if (shouldSkipSaveDrafts) return false
        val text = editText.text.toString()
        val fragment = newTopicMediaFragment
        if (TextUtils.isEmpty(text) && !fragment.hasMedia()) {
            clearDraft()
            return false
        }
        var draftChanged = false
        val editor = preferences.edit()
        if (text != preferences.getString(KEY_TOPIC_DRAFTS_TEXT, null)) {
            editor.putString(KEY_TOPIC_DRAFTS_TEXT, text)
            draftChanged = true
        }
        draftChanged = draftChanged or fragment.saveDraft()
        editor.apply()
        return draftChanged
    }

    private val newTopicMediaFragment: NewTopicMediaFragment
        get() = supportFragmentManager.findFragmentById(R.id.new_topic_media) as NewTopicMediaFragment

    private fun clearDraft() {
        val editor = preferences.edit()
        editor.remove(KEY_TOPIC_DRAFTS_TEXT)
        editor.apply()
        newTopicMediaFragment.clearDraft()
    }

    override fun onDestroy() {
        if (draftsSaved) {
            Toast.makeText(this, R.string.drafts_saved, Toast.LENGTH_SHORT).show()
        }
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.send -> {
                postTopic()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun postTopic() {
        val body = editText.text.toString()
        val location = Utils.getCachedLocation(this)
        if (TextUtils.isEmpty(body)) {
            editText.error = getString(R.string.no_content)
            return
        }
        val newTopic = NewTopic()
        newTopic.body(body)
        val skill = topicSpinner.selectedItem as Skill?
        newTopic.skillId(skill?.id)
        newTopic.location(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
        task {
            val yep = YepAPIFactory.getInstance(this@NewTopicActivity, account)
            newTopicMediaFragment.uploadMedia(yep, newTopic)
            return@task yep.postTopic(newTopic)
        }.successUi {
            finishPosting()
        }.failUi {
            dismissUploadingDialog()
            Toast.makeText(this, R.string.unable_to_create_topic, Toast.LENGTH_SHORT).show()
            Log.w(Constants.LOGTAG, it)
        }
        val df = ProgressDialogFragment()
        df.isCancelable = false
        df.show(supportFragmentManager, FRAGMENT_TAG_POSTING_TOPIC)
    }

    private fun dismissUploadingDialog() {
        mDismissUploadingDialogRunnable = Runnable {
            val fm = supportFragmentManager
            val f = fm.findFragmentByTag(FRAGMENT_TAG_POSTING_TOPIC)
            if (f is DialogFragment) {
                f.dismiss()
            }
        }
        invokeFragmentRunnable()
    }

    private fun finishPosting() {
        shouldSkipSaveDrafts = true
        clearDraft()
        Toast.makeText(this, R.string.topic_posted, Toast.LENGTH_SHORT).show()
        if (!isFinishing) {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_new_topic, menu)
        return true
    }


    private class SkillSpinnerAdapter(activity: NewTopicActivity) : ArrayAdapter<Skill>(activity, android.R.layout.simple_expandable_list_item_1) {
        init {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            val textView = view.findViewById(android.R.id.text1) as TextView
            if (TextUtils.isEmpty(getItem(position).id)) {
                textView.setText(R.string.none)
            } else {
                textView.text = Utils.getDisplayName(getItem(position))
            }
            return view
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val textView = view.findViewById(android.R.id.text1) as TextView
            if (TextUtils.isEmpty(getItem(position).id)) {
                textView.setText(R.string.choose_skill)
            } else {
                textView.text = Utils.getDisplayName(getItem(position))
            }
            return view
        }

        fun findPositionBySkillId(id: String): Int {
            var i = 0
            val j = count
            while (i < j) {
                if (StringUtils.equals(id, getItem(i).id)) return i
                i++
            }
            return ListView.INVALID_POSITION
        }
    }

    companion object {

        val TYPE_PHOTOS_TEXT = "photos_text"
        val TYPE_AUDIO = "audio"
        val TYPE_LOCATION = "location"

        private val FRAGMENT_TAG_POSTING_TOPIC = "posting_topic"
        private val KEY_TOPIC_DRAFTS_TEXT = "topic_drafts_text"
    }
}
