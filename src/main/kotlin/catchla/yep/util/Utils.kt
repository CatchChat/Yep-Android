/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.util

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.UriMatcher
import android.content.res.Resources
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.util.Base64
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import catchla.yep.BuildConfig
import catchla.yep.Constants.*
import catchla.yep.R
import catchla.yep.activity.SettingsActivity
import catchla.yep.extension.getUser
import catchla.yep.fragment.SettingsDetailsFragment
import catchla.yep.model.*
import catchla.yep.provider.YepDataStore.*
import catchla.yep.view.DribbbleProviderWidgetContainer
import catchla.yep.view.GithubProviderWidgetContainer
import catchla.yep.view.InstagramProviderWidgetContainer
import catchla.yep.view.holder.FriendViewHolder
import okhttp3.MediaType
import okio.ByteString
import org.mariotaku.ktextension.indexOf
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.util.*
import java.util.regex.Pattern

/**
 * Created by mariotaku on 15/5/5.
 */
object Utils {

    private val DATABASE_URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH)
    private val TABLE_NAMES = SparseArray<String>()
    private val sRandom = Random()

    init {
        DATABASE_URI_MATCHER.addURI(BuildConfig.APPLICATION_ID, Friendships.CONTENT_PATH, TABLE_ID_FRIENDSHIPS)
        DATABASE_URI_MATCHER.addURI(BuildConfig.APPLICATION_ID, Messages.CONTENT_PATH, TABLE_ID_MESSAGES)
        DATABASE_URI_MATCHER.addURI(BuildConfig.APPLICATION_ID, Conversations.CONTENT_PATH, TABLE_ID_CONVERSATIONS)
        DATABASE_URI_MATCHER.addURI(BuildConfig.APPLICATION_ID, Conversations.CONTENT_PATH_SEARCH + "/*/*", VIRTUAL_TABLE_ID_CONVERSATIONS)

        DATABASE_URI_MATCHER.addURI(BuildConfig.APPLICATION_ID, Messages.CONTENT_PATH + "/*/*", TABLE_ID_CONVERSATION_MESSAGES)
        TABLE_NAMES.append(TABLE_ID_FRIENDSHIPS, Friendships.TABLE_NAME)
        TABLE_NAMES.append(TABLE_ID_MESSAGES, Messages.TABLE_NAME)
        TABLE_NAMES.append(TABLE_ID_CONVERSATIONS, Conversations.TABLE_NAME)
    }

    val PATTERN_XML_RESOURCE_IDENTIFIER = Pattern.compile("res/xml/([\\w_]+)\\.xml")

    val PATTERN_RESOURCE_IDENTIFIER = Pattern.compile("@([\\w_]+)/([\\w_]+)")


    fun getResId(context: Context?, string: String?): Int {
        if (context == null || string == null) return 0
        var m = PATTERN_RESOURCE_IDENTIFIER.matcher(string)
        val res = context.resources
        if (m.matches()) return res.getIdentifier(m.group(2), m.group(1), context.packageName)
        m = PATTERN_XML_RESOURCE_IDENTIFIER.matcher(string)
        if (m.matches()) return res.getIdentifier(m.group(1), "xml", context.packageName)
        return 0
    }


    fun getCurrentAccount(context: Context): Account? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val currentAccountName = prefs.getString(KEY_CURRENT_ACCOUNT, null)
        if (TextUtils.isEmpty(currentAccountName)) return null
        val am = AccountManager.get(context)
        for (account in am.getAccountsByType(ACCOUNT_TYPE)) {
            if (currentAccountName == account.name) return account
        }
        return null
    }

    fun getCurrentAccountUser(context: Context): User? {
        val account = getCurrentAccount(context)
        return if (account != null) getAccountUser(context, account) else null
    }


    fun getImageMimeType(image: File?): String? {
        if (image == null) return null
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        BitmapFactory.decodeFile(image.path, o)
        return o.outMimeType
    }

    fun getAccountUser(context: Context, account: Account): User {
        return account.getUser(context)
    }

    fun setCurrentAccount(context: Context, account: Account?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        if (account != null) {
            editor.putString(KEY_CURRENT_ACCOUNT, account.name)
        } else {
            editor.remove(KEY_CURRENT_ACCOUNT)
        }
        editor.apply()
    }

    fun closeSilently(s: Closeable?) {
        if (s == null) return
        try {
            s.close()
        } catch (ignored: IOException) {
        }

    }

    fun closeSilently(s: Cursor?) {
        if (s == null) return
        s.close()
    }

    fun formatSameDayTime(context: Context?, timestamp: Long): String? {
        if (context == null) return null
        if (DateUtils.isToday(timestamp))
        //noinspection deprecation
            return DateUtils.formatDateTime(context, timestamp,
                    if (DateFormat.is24HourFormat(context))
                        DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_24HOUR
                    else
                        DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_12HOUR)
        return DateUtils.formatDateTime(context, timestamp, DateUtils.FORMAT_SHOW_DATE)
    }

    fun writeUserToUserData(user: User, userData: Bundle) {
        userData.putString(USER_DATA_ID, user.id)
        userData.putString(USER_DATA_AVATAR, user.avatarUrl)
        userData.putString(USER_DATA_NICKNAME, user.nickname)
        userData.putString(USER_DATA_USERNAME, user.username)
        userData.putString(USER_DATA_PHONE_NUMBER, user.mobile)
        userData.putString(USER_DATA_COUNTRY_CODE, user.phoneCode)
        userData.putString(USER_DATA_INTRODUCTION, user.introduction)
        userData.putString(USER_DATA_WEBSITE, user.websiteUrl)
        userData.putString(USER_DATA_LEARNING_SKILLS, JsonSerializer.serialize(user.learningSkills, Skill::class.java))
        userData.putString(USER_DATA_MASTER_SKILLS, JsonSerializer.serialize(user.masterSkills, Skill::class.java))
        userData.putString(USER_DATA_PROVIDERS, JsonSerializer.serialize(user.providers, Provider::class.java))
        userData.putString(USER_DATA_BADGE, user.badge?.value)
    }

    fun inflateProviderItemView(context: Context, fm: FragmentManager,
                                inflater: LayoutInflater, provider: Provider,
                                parent: ViewGroup, addWidget: Boolean,
                                account: Account, user: User): View {
        val name = provider.name
        val view = inflater.inflate(R.layout.list_item_provider_common, parent, false)
        if (addWidget) {
            val widgetFrame = view.findViewById(R.id.providerWidgetFrame) as FrameLayout
            when (name) {
                "instagram" -> {
                    val widget = inflater.inflate(R.layout.provider_widget_instagram, widgetFrame, false) as InstagramProviderWidgetContainer
                    widget.account = account
                    widget.user = user
                    widgetFrame.addView(widget)
                }
                "dribbble" -> {
                    val widget = inflater.inflate(R.layout.provider_widget_dribbble, widgetFrame, false) as DribbbleProviderWidgetContainer
                    widget.account = account
                    widget.user = user
                    widgetFrame.addView(widget)
                }
                "github" -> {
                    val widget = inflater.inflate(R.layout.provider_widget_github, widgetFrame, false) as GithubProviderWidgetContainer
                    widget.account = account
                    widget.user = user
                    widgetFrame.addView(widget)
                }
            }
        }
        val iconView = view.findViewById(android.R.id.icon) as ImageView
        val titleView = view.findViewById(android.R.id.title) as TextView
        titleView.text = Provider.getProviderName(context, name)
        val icon = Provider.getProviderIcon(context, name)
        if (icon != 0) {
            iconView.setImageResource(icon)
        } else {
            iconView.setImageDrawable(null)
        }
        if (provider.isSupported) {
            val providerColor = Provider.getProviderColor(context, name)
            iconView.setColorFilter(providerColor, PorterDuff.Mode.SRC_ATOP)
            titleView.setTextColor(providerColor)
        } else {
            val secondaryColor = ThemeUtils.getColorFromAttribute(context,
                    android.R.attr.textColorSecondary, 0)
            iconView.setColorFilter(secondaryColor, PorterDuff.Mode.SRC_ATOP)
            titleView.setTextColor(secondaryColor)
        }
        return view
    }

    fun isMySelf(context: Context, account: Account, user: User): Boolean {
        val accountUser = getAccountUser(context, account)
        return user.id == accountUser.id
    }

    fun inflateSkillItemView(context: Context, inflater: LayoutInflater, skill: Skill, parent: ViewGroup): View {
        val view = inflater.inflate(R.layout.layout_skill_label_button, parent, false)
        val button = view.findViewById(R.id.skillButton) as TextView
        button.text = skill.nameString
        return view
    }


    fun getErrorMessage(exception: Exception): String? {
        if (exception is YepException) {
            return exception.error
        }
        return null
    }

    fun findSkill(skills: List<Skill>?, id: String?): Skill? {
        if (skills == null || id == null) return null
        for (skill in skills) {
            if (id == skill.id) return skill
        }
        return null
    }

    fun saveUserInfo(context: Context, account: Account, user: User) {
        val am = AccountManager.get(context)
        if (!TextUtils.equals(user.id, am.getUserData(account, USER_DATA_ID))) return
        val userData = Bundle()
        writeUserToUserData(user, userData)
        for (key in userData.keySet()) {
            am.setUserData(account, key, userData.getString(key))
        }
    }

    fun getCachedLocation(context: Context): Location? {
        var location: Location? = null
        try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            } catch (ignore: SecurityException) {

            }

            if (location != null) return location
            try {
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            } catch (ignore: SecurityException) {

            }

        } catch (ignore: Exception) {
        }

        return location
    }

    fun openSettings(context: Context, account: Account) {
        val intent = Intent(context, SettingsActivity::class.java)
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsDetailsFragment::class.java.name)
        val args = Bundle()
        args.putInt(EXTRA_RESID, R.xml.pref_general)
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_ARGUMENTS, args)
        intent.putExtra(EXTRA_ACCOUNT, account)
        context.startActivity(intent)
    }


    fun getTableName(uri: Uri): String? {
        return getTableName(getTableId(uri))
    }

    fun getTableName(id: Int): String? {
        return TABLE_NAMES.get(id)
    }

    fun getTableId(uri: Uri): Int {
        return DATABASE_URI_MATCHER.match(uri)
    }

    fun getTime(date: Date?): Long {
        if (date == null) return 0
        return date.time
    }

    fun getMetadataBitmap(res: Resources, metadata: FileAttachment.ImageMetadata): BitmapDrawable? {
        val bytes: ByteArray
        try {
            bytes = Base64.decode(metadata.blurredThumbnail, Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {
            return null
        }

        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
        return BitmapDrawable(res, bitmap)
    }

    fun getConversationName(conversation: Conversation): String {
        val recipientType = conversation.recipientType
        if (Message.RecipientType.CIRCLE == recipientType) {
            val circle = conversation.circle
            if (circle == null || circle.topic == null || TextUtils.isEmpty(circle.topic.body)) {
                return "Circle"
            } else {
                return circle.topic.body
            }
        } else if (Message.RecipientType.USER == recipientType) {
            val user = conversation.user ?: return "User"
            return getDisplayName(user)
        }
        throw UnsupportedOperationException("Unknown recipientType " + recipientType)
    }

    fun getDisplayName(user: User): String {
        if (!TextUtils.isEmpty(user.nickname))
            return user.nickname
        else if (!TextUtils.isEmpty(user.contactName))
            return user.contactName
        else
            return user.username
    }

    fun getAccountId(context: Context, account: Account?): String? {
        if (account == null) return null
        val am = AccountManager.get(context)
        return am.getUserData(account, USER_DATA_ID)
    }

    fun getDistanceString(distanceMeters: Float): String {
        if (distanceMeters < 1000) {
            return String.format(Locale.US, "%.0f m", distanceMeters)
        }
        return String.format(Locale.US, "%.1f km", distanceMeters / 1000f)
    }

    fun getDisplayName(skill: Skill): String {
        return if (TextUtils.isEmpty(skill.nameString)) skill.name else skill.nameString
    }

    fun hasSkill(user: User, skill: Skill): Boolean {
        val learning = user.learningSkills
        val mastered = user.masterSkills
        if (learning != null && learning.contains(skill)) {
            return true
        }
        if (mastered != null && mastered.contains(skill)) {
            return true
        }
        return false
    }

    fun getUserLink(user: User): String {
        return "http://soyep.com/" + user.username
    }

    fun getConversationAvatarUrl(conversation: Conversation): String? {
        val recipientType = conversation.recipientType
        if (Message.RecipientType.CIRCLE == recipientType) {
            val circle = conversation.circle
            if (circle != null) {
                val topic = circle.topic
                if (topic != null) {
                    return getAttachmentThumb(topic.attachments, topic.kind)
                }
            }
            return null
        } else if (Message.RecipientType.USER == recipientType) {
            return conversation.user.avatarUrl
        }
        throw UnsupportedOperationException("Unknown recipientType " + recipientType)
    }

    private fun getAttachmentThumb(attachments: List<Attachment>?, attachmentKind: String?): String? {
        if (attachments == null || attachmentKind == null) return null
        for (attachment in attachments) {
            if (attachment is FileAttachment) {
                if (Attachment.Kind.IMAGE == attachmentKind) {
                    return attachment.file.url
                }
            }
        }
        return null
    }

    val defaultAvatarUrl: String
        get() = Uri.fromParts(ContentResolver.SCHEME_ANDROID_RESOURCE,
                "//" + BuildConfig.APPLICATION_ID + "/" + R.drawable.ic_profile_image_default, null).toString()

    fun getFilename(file: File, mimeType: String): String {
        val name = file.name
        val dotIndex = name.indexOf(".")
        val extension: String
        when (mimeType) {
            "audio/mp4" -> {
                extension = "m4a"
            }
            else -> {
                val mimeTypeMap = MimeTypeMap.getSingleton()
                extension = mimeTypeMap.getExtensionFromMimeType(mimeType) ?: MediaType.parse(mimeType).subtype() ?: "bin"
            }
        }
        if (dotIndex < 0) {
            return name + "." + extension
        }
        return name.substring(0, dotIndex + 1) + extension
    }

    fun getMarkerBitmap(context: Context): Bitmap {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_map_marker)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        return bitmap
    }

    fun generateRandomId(length: Int): String {
        val buf = ByteArray(length)
        sRandom.nextBytes(buf)
        return ByteString.of(*buf).hex()
    }

    fun getColorDark(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= 0.9f
        return Color.HSVToColor(hsv)
    }

    fun openUri(activity: Activity, uri: Uri) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ThemeUtils.getColorPrimary(activity))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(activity, uri)
    }
}

fun String.highlightedString(highlight: String?): CharSequence {
    if (highlight == null || highlight.isEmpty()) return this
    val spannable = SpannableString.valueOf(this)
    indexOf(highlight, ignoreCase = true) {
        spannable.setSpan(FriendViewHolder.HighlightSpan, it, it + highlight.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return@indexOf true
    }
    return spannable
}