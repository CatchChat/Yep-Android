package catchla.yep.activity

import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import catchla.yep.Constants
import catchla.yep.Constants.*
import catchla.yep.R
import catchla.yep.loader.UserLoader
import catchla.yep.model.*
import catchla.yep.provider.YepDataStore.Friendships
import catchla.yep.util.JsonSerializer
import catchla.yep.util.Utils
import catchla.yep.util.YepAPIFactory
import catchla.yep.util.support.WindowSupport
import catchla.yep.util.task.UpdateProfileTask
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.layout_content_user.*
import nl.komponents.kovenant.task
import org.apache.commons.lang3.StringUtils
import org.mariotaku.ktextension.setMenuGroupAvailability
import org.mariotaku.sqliteqb.library.Expression
import java.util.*

class UserActivity : SwipeBackContentActivity(), Constants, View.OnClickListener, LoaderManager.LoaderCallbacks<TaskResponse<User>>, UpdateProfileTask.Callback {

    private val REQUEST_SELECT_MASTER_SKILLS = 111
    private val REQUEST_SELECT_LEARNING_SKILLS = 112

    private val currentUser: User?
        get() = if (intent.hasExtra(EXTRA_USER)) {
            intent.getParcelableExtra<User>(EXTRA_USER)
        } else {
            Utils.getAccountUser(this, account)
        }

    private var updateProfileTask: UpdateProfileTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        val currentUser = this.currentUser
        if (currentUser == null) {
            finish()
            return
        }
        fab.setOnClickListener(this)
        userTopics.setOnClickListener(this)

        title = Utils.getDisplayName(currentUser)
        displayUser(currentUser)

        WindowSupport.setStatusBarColor(window, 0x19000000)
        ViewCompat.setOnApplyWindowInsetsListener(coordinatorLayout) { view, insets ->
            toolbarShadow.setPadding(0, insets.systemWindowInsetTop, 0, 0)
            val lp = toolbar.layoutParams as ViewGroup.MarginLayoutParams
            lp.topMargin = insets.systemWindowInsetTop
            toolbar.layoutParams = lp
            return@setOnApplyWindowInsetsListener insets.consumeSystemWindowInsets()
        }

        supportLoaderManager.initLoader(0, null, this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun displayUser(user: User?) {
        if (user == null) return
        val oldUser: User? = intent.getParcelableExtra(EXTRA_USER)
        val providersChanged = (user.providers?.equals(oldUser?.providers) ?: false) != true
        intent.putExtra(EXTRA_USER, user)
        val avatarUrl = user.avatarUrl
        imageLoader.displayProfileImage(avatarUrl, profileImage)
        val username = user.username
        val introduction = user.introduction
        name.text = user.nickname
        if (TextUtils.isEmpty(username)) {
            this.username.setText(R.string.no_username)
        } else {
            this.username.text = username
        }
        if (TextUtils.isEmpty(introduction)) {
            this.introduction.setText(R.string.no_introduction_yet)
        } else {
            this.introduction.text = introduction
        }

        val skillOnClickListener = View.OnClickListener { v ->
            val skill = v.tag as Skill
            val intent = Intent(this@UserActivity, SkillUsersActivity::class.java)
            intent.putExtra(EXTRA_ACCOUNT, account)
            intent.putExtra(EXTRA_SKILL, skill)
            startActivity(intent)
        }

        val isMySelf = Utils.isMySelf(this, account, user)

        if (isMySelf) {
            fab.setImageResource(R.drawable.ic_action_edit)
        } else {
            fab.setImageResource(R.drawable.ic_action_chat)
        }

        val inflater = this@UserActivity.layoutInflater

        learningSkills.removeAllViews()
        user.learningSkills?.forEach { skill ->
            val view = Utils.inflateSkillItemView(this@UserActivity, inflater, skill, learningSkills)
            val skillButton = view.findViewById(R.id.skill_button)
            skillButton.tag = skill
            skillButton.setOnClickListener(skillOnClickListener)
            learningSkills.addView(view)
        }
        if (isMySelf) {
            learningLabel.setOnClickListener {
                val intent = Intent(this@UserActivity, SkillSelectorActivity::class.java)
                intent.putParcelableArrayListExtra(EXTRA_SKILLS, ArrayList(user.learningSkills))
                startActivityForResult(intent, REQUEST_SELECT_LEARNING_SKILLS)
            }
        } else {
            //TODO: Add empty view
        }
        masterSkills.removeAllViews()
        user.masterSkills?.forEach { skill ->
            val view = Utils.inflateSkillItemView(this@UserActivity, inflater, skill, masterSkills)
            val skillButton = view.findViewById(R.id.skill_button)
            skillButton.tag = skill
            skillButton.setOnClickListener(skillOnClickListener)
            masterSkills.addView(view)
        }
        if (isMySelf) {
            masterLabel.setOnClickListener {
                val intent = Intent(this@UserActivity, SkillSelectorActivity::class.java)
                intent.putParcelableArrayListExtra(EXTRA_SKILLS, ArrayList(user.learningSkills))
                startActivityForResult(intent, REQUEST_SELECT_MASTER_SKILLS)
            }
        } else {
            //TODO: Add empty view
        }
        val providers = user.providers
        if (providersChanged) {
            providersContainer.removeAllViews()

            val websiteUrl = user.websiteUrl
            val hasWebsite = !TextUtils.isEmpty(websiteUrl)

            val providerOnClickListener = View.OnClickListener { v ->
                val provider = v.tag as Provider
                val intent: Intent
                if (provider.isSupported) {
                    if (Provider.PROVIDER_BLOG == provider.name && user.websiteUrl != null) {
                        // TODO open web address
                        Utils.openUri(this, Uri.parse(user.websiteUrl))
                        return@OnClickListener
                    }
                    intent = Intent(this@UserActivity, ProviderContentActivity::class.java)
                } else if (isMySelf) {
                    if (Provider.PROVIDER_BLOG == provider.name) {
                        // TODO open url editor
                        return@OnClickListener
                    }
                    intent = Intent(this@UserActivity, ProviderOAuthActivity::class.java)
                } else {
                    return@OnClickListener
                }
                intent.putExtra(EXTRA_PROVIDER_NAME, provider.name)
                intent.putExtra(EXTRA_USER, user)
                intent.putExtra(EXTRA_ACCOUNT, account)
                startActivity(intent)
            }
            if (hasWebsite || isMySelf) {
                val provider = Provider("blog", hasWebsite)
                val view = Utils.inflateProviderItemView(this@UserActivity, supportFragmentManager,
                        inflater, provider, providersContainer, false, account, user)
                view.tag = provider
                view.setOnClickListener(providerOnClickListener)
                providersContainer.addView(view)
            }
            if (providers != null) {
                for (provider in providers) {
                    if (!provider.isSupported) continue
                    val view = Utils.inflateProviderItemView(this@UserActivity, supportFragmentManager,
                            inflater, provider, providersContainer, true, account, user)
                    view.tag = provider
                    view.setOnClickListener(providerOnClickListener)
                    providersContainer.addView(view)
                }
                if (isMySelf) for (provider in providers) {
                    if (provider.isSupported) continue
                    val view = Utils.inflateProviderItemView(this@UserActivity, supportFragmentManager,
                            inflater, provider, providersContainer, false, account, user)
                    view.tag = provider
                    view.setOnClickListener(providerOnClickListener)
                    providersContainer.addView(view)
                }
            }
        }
        if (providersContainer.childCount > 0) {
            providersDivider.visibility = View.VISIBLE
        } else {
            providersDivider.visibility = View.GONE
        }

        topicsWidgetFrame.account = account
        topicsWidgetFrame.user = user
        topicsWidgetFrame.startTask()
    }

    override fun onClick(v: View) {
        when (v) {
            fab -> {
                if (Utils.isMySelf(this, account, currentUser!!)) {
                    val intent = Intent(this, ProfileEditorActivity::class.java)
                    intent.putExtra(EXTRA_ACCOUNT, account)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra(EXTRA_CONVERSATION, Conversation.fromUser(currentUser,
                            Utils.getAccountId(this, account)))
                    startActivity(intent)
                }
            }
            userTopics -> {
                val intent = Intent(this, UserTopicsActivity::class.java)
                intent.putExtra(EXTRA_ACCOUNT, account)
                intent.putExtra(EXTRA_USER, currentUser)
                startActivity(intent)
            }
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<TaskResponse<User>> {
        return UserLoader(this, account, currentUser!!.id)
    }

    override fun onLoadFinished(loader: Loader<TaskResponse<User>>, data: TaskResponse<User>) {
        val user = data.data ?: return
        val account = account
        val accountId = Utils.getAccountId(this, account)
        displayUser(user)
        if (StringUtils.equals(user.id, accountId)) {
            Utils.saveUserInfo(this@UserActivity, account, user)
        } else {
            task {
                val values = ContentValues()
                values.put(Friendships.FRIEND, JsonSerializer.serialize(user))
                val cr = contentResolver
                val where = Expression.and(Expression.equalsArgs(Friendships.ACCOUNT_ID),
                        Expression.equalsArgs(Friendships.USER_ID)).sql
                cr.update(Friendships.CONTENT_URI, values, where, arrayOf(accountId, user.id))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_user, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val isMySelf = Utils.isMySelf(this, account, currentUser!!)
        menu.setMenuGroupAvailability(R.id.group_menu_friend, !isMySelf)
        menu.setMenuGroupAvailability(R.id.group_menu_myself, isMySelf)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val currentUser = this.currentUser ?: return false
        when (item.itemId) {
            R.id.settings -> {
                Utils.openSettings(this, account)
                return true
            }
            R.id.share -> {
                val user = currentUser
                if (!TextUtils.isEmpty(user.username)) {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text_template,
                            Utils.getDisplayName(currentUser), Utils.getUserLink(user)))
                    startActivity(Intent.createChooser(intent, getString(R.string.share)))
                } else {
                    val df = SetUsernameDialogFragment()
                    df.show(supportFragmentManager, "set_username")
                }
                return true
            }
            R.id.block_user -> {
                task {
                    with(YepAPIFactory.getInstance(this@UserActivity, account)) {
                        blockUser(currentUser.id)
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onLoaderReset(loader: Loader<TaskResponse<User>>) {

    }


    override val isTintBarEnabled: Boolean
        get() = false


    override fun onProfileUpdated(user: User) {
        displayUser(user)
    }

    class SetUsernameDialogFragment : DialogFragment(), DialogInterface.OnClickListener {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.set_username)
            builder.setNegativeButton(android.R.string.cancel, null)
            builder.setPositiveButton(android.R.string.ok, this)
            builder.setView(R.layout.dialog_set_username)
            return builder.create()
        }

        override fun onClick(dialog: DialogInterface, which: Int) {
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val editUsername = (dialog as Dialog).findViewById(R.id.edit_username) as EditText
                    (activity as UserActivity).setUsername(editUsername.text.toString())
                }
            }
        }
    }

    private fun setUsername(username: String) {
        val update = ProfileUpdate()
        update.setUsername(username)
        if (updateProfileTask == null || updateProfileTask!!.status != AsyncTask.Status.RUNNING) {
            updateProfileTask = UpdateProfileTask(this, account, update, null)
            updateProfileTask!!.execute()
        }
    }

}
