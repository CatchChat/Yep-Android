/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import catchla.yep.BuildConfig;
import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.activity.SettingsActivity;
import catchla.yep.fragment.SettingsDetailsFragment;
import catchla.yep.model.Attachment;
import catchla.yep.model.Circle;
import catchla.yep.model.Conversation;
import catchla.yep.model.FileAttachment;
import catchla.yep.model.Message;
import catchla.yep.model.Provider;
import catchla.yep.model.Skill;
import catchla.yep.model.Topic;
import catchla.yep.model.User;
import catchla.yep.model.YepException;
import catchla.yep.provider.YepDataStore.Conversations;
import catchla.yep.provider.YepDataStore.Friendships;
import catchla.yep.provider.YepDataStore.Messages;
import okhttp3.MediaType;
import okio.ByteString;

/**
 * Created by mariotaku on 15/5/5.
 */
public class Utils implements Constants {

    private static final UriMatcher DATABASE_URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final SparseArray<String> TABLE_NAMES = new SparseArray<>();
    private static final Random sRandom = new Random();

    static {
        DATABASE_URI_MATCHER.addURI(BuildConfig.APPLICATION_ID, Friendships.CONTENT_PATH, TABLE_ID_FRIENDSHIPS);
        DATABASE_URI_MATCHER.addURI(BuildConfig.APPLICATION_ID, Messages.CONTENT_PATH, TABLE_ID_MESSAGES);
        DATABASE_URI_MATCHER.addURI(BuildConfig.APPLICATION_ID, Conversations.CONTENT_PATH, TABLE_ID_CONVERSATIONS);

        DATABASE_URI_MATCHER.addURI(BuildConfig.APPLICATION_ID, Messages.CONTENT_PATH + "/*/*", TABLE_ID_CONVERSATION_MESSAGES);
        TABLE_NAMES.append(TABLE_ID_FRIENDSHIPS, Friendships.TABLE_NAME);
        TABLE_NAMES.append(TABLE_ID_MESSAGES, Messages.TABLE_NAME);
        TABLE_NAMES.append(TABLE_ID_CONVERSATIONS, Conversations.TABLE_NAME);
    }

    public static final Pattern PATTERN_XML_RESOURCE_IDENTIFIER = Pattern.compile("res/xml/([\\w_]+)\\.xml");

    public static final Pattern PATTERN_RESOURCE_IDENTIFIER = Pattern.compile("@([\\w_]+)/([\\w_]+)");

    public static int getInsetsTopWithoutActionBarHeight(Context context, int top) {
        final int actionBarHeight;
        if (context instanceof AppCompatActivity) {
            actionBarHeight = getActionBarHeight(((AppCompatActivity) context).getSupportActionBar());
        } else {
            return top;
        }
        if (actionBarHeight > top) {
            return top;
        }
        return top - actionBarHeight;
    }


    public static int getActionBarHeight(@Nullable ActionBar actionBar) {
        if (actionBar == null) return 0;
        final Context context = actionBar.getThemedContext();
        final TypedValue tv = new TypedValue();
        final int height = actionBar.getHeight();
        if (height > 0) return height;
        if (context.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return 0;
    }


    public static int getInsetsTopWithoutActionBarHeight(Context context, int top, int actionBarHeight) {
        if (actionBarHeight > top) {
            return top;
        }
        return top - actionBarHeight;
    }


    public static int getResId(final Context context, final String string) {
        if (context == null || string == null) return 0;
        Matcher m = PATTERN_RESOURCE_IDENTIFIER.matcher(string);
        final Resources res = context.getResources();
        if (m.matches()) return res.getIdentifier(m.group(2), m.group(1), context.getPackageName());
        m = PATTERN_XML_RESOURCE_IDENTIFIER.matcher(string);
        if (m.matches()) return res.getIdentifier(m.group(1), "xml", context.getPackageName());
        return 0;
    }


    @Nullable
    public static Account getCurrentAccount(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String currentAccountName = prefs.getString(KEY_CURRENT_ACCOUNT, null);
        if (TextUtils.isEmpty(currentAccountName)) return null;
        final AccountManager am = AccountManager.get(context);
        for (Account account : am.getAccountsByType(ACCOUNT_TYPE)) {
            if (currentAccountName.equals(account.name)) return account;
        }
        return null;
    }

    @Nullable
    public static User getCurrentAccountUser(Context context) {
        final Account account = getCurrentAccount(context);
        return account != null ? getAccountUser(context, account) : null;
    }


    public static String getImageMimeType(final File image) {
        if (image == null) return null;
        final BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), o);
        return o.outMimeType;
    }

    @NonNull
    public static User getAccountUser(Context context, @NonNull Account account) {
        final AccountManager am = AccountManager.get(context);
        final User user = new User();
        user.setId(am.getUserData(account, USER_DATA_ID));
        user.setNickname(am.getUserData(account, USER_DATA_NICKNAME));
        user.setAvatarUrl(am.getUserData(account, USER_DATA_AVATAR));
        user.setPhoneCode(am.getUserData(account, USER_DATA_COUNTRY_CODE));
        user.setMobile(am.getUserData(account, USER_DATA_PHONE_NUMBER));
        user.setIntroduction(am.getUserData(account, USER_DATA_INTRODUCTION));
        user.setUsername(am.getUserData(account, USER_DATA_USERNAME));
        user.setWebsiteUrl(am.getUserData(account, USER_DATA_WEBSITE));
        final String learningJson = am.getUserData(account, USER_DATA_LEARNING_SKILLS);
        user.setLearningSkills(JsonSerializer.parseList(learningJson, Skill.class));
        final String masterJson = am.getUserData(account, USER_DATA_MASTER_SKILLS);
        user.setMasterSkills(JsonSerializer.parseList(masterJson, Skill.class));
        final String providersJson = am.getUserData(account, USER_DATA_PROVIDERS);
        user.setProviders(JsonSerializer.parseList(providersJson, Provider.class));
        return user;
    }

    public static void setCurrentAccount(Context context, Account account) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        if (account != null) {
            editor.putString(KEY_CURRENT_ACCOUNT, account.name);
        } else {
            editor.remove(KEY_CURRENT_ACCOUNT);
        }
        editor.apply();
    }

    public static void closeSilently(final Closeable is) {
        if (is == null) return;
        try {
            is.close();
        } catch (IOException ignored) {
        }
    }

    public static void closeSilently(final Cursor is) {
        if (is == null) return;
        is.close();
    }

    public static String formatSameDayTime(final Context context, final long timestamp) {
        if (context == null) return null;
        if (DateUtils.isToday(timestamp))
            //noinspection deprecation
            return DateUtils.formatDateTime(context, timestamp,
                    DateFormat.is24HourFormat(context) ? DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR
                            : DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_12HOUR);
        return DateUtils.formatDateTime(context, timestamp, DateUtils.FORMAT_SHOW_DATE);
    }

    public static void writeUserToUserData(final User user, final Bundle userData) {
        userData.putString(USER_DATA_ID, user.getId());
        userData.putString(USER_DATA_AVATAR, user.getAvatarUrl());
        userData.putString(USER_DATA_NICKNAME, user.getNickname());
        userData.putString(USER_DATA_USERNAME, user.getUsername());
        userData.putString(USER_DATA_PHONE_NUMBER, user.getMobile());
        userData.putString(USER_DATA_COUNTRY_CODE, user.getPhoneCode());
        userData.putString(USER_DATA_INTRODUCTION, user.getIntroduction());
        userData.putString(USER_DATA_WEBSITE, user.getWebsiteUrl());
        userData.putString(USER_DATA_LEARNING_SKILLS, JsonSerializer.serialize(user.getLearningSkills(), Skill.class));
        userData.putString(USER_DATA_MASTER_SKILLS, JsonSerializer.serialize(user.getMasterSkills(), Skill.class));
        userData.putString(USER_DATA_PROVIDERS, JsonSerializer.serialize(user.getProviders(), Provider.class));
    }

    public static View inflateProviderItemView(final Context context, final LayoutInflater inflater, final Provider provider, final ViewGroup parent) {
        final String name = provider.getName();
        final View view = inflater.inflate(R.layout.list_item_provider_common, parent, false);
        if ("dribbble".equals(name)) {
        } else if ("github".equals(name)) {
        }
        final ImageView iconView = (ImageView) view.findViewById(android.R.id.icon);
        final TextView titleView = (TextView) view.findViewById(android.R.id.title);
        titleView.setText(Provider.getProviderName(context, name));
        final int icon = Provider.getProviderIcon(context, name);
        if (icon != 0) {
            iconView.setImageResource(icon);
        } else {
            iconView.setImageDrawable(null);
        }
        if (provider.isSupported()) {
            final int providerColor = Provider.getProviderColor(context, name);
            iconView.setColorFilter(providerColor, PorterDuff.Mode.SRC_ATOP);
            titleView.setTextColor(providerColor);
        } else {
            final int secondaryColor = ThemeUtils.getColorFromAttribute(context,
                    android.R.attr.textColorSecondary, 0);
            iconView.setColorFilter(secondaryColor, PorterDuff.Mode.SRC_ATOP);
            titleView.setTextColor(secondaryColor);
        }
        return view;
    }

    public static boolean isMySelf(final Context context, final Account account, final User user) {
        final User accountUser = getAccountUser(context, account);
        if (accountUser == null || user == null) return false;
        return user.getId().equals(accountUser.getId());
    }

    public static View inflateSkillItemView(final Context context, final LayoutInflater inflater, final Skill skill, final ViewGroup parent) {
        final View view = inflater.inflate(R.layout.layout_skill_label_button, parent, false);
        final TextView button = (TextView) view.findViewById(R.id.skill_button);
        button.setText(skill.getNameString());
        return view;
    }


    public static void setCompatToolbarOverlayAlpha(FragmentActivity activity, float alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) return;
//        final View windowOverlay = activity.findViewById(R.id.window_overlay);
//        if (windowOverlay != null) {
//            windowOverlay.setAlpha(alpha);
//            return;
//        }
        final Drawable drawable = ThemeUtils.getCompatToolbarOverlay(activity);
        if (drawable == null) return;
        drawable.setAlpha(Math.round(alpha * 255));
    }

    public static String getErrorMessage(final Exception exception) {
        if (exception instanceof YepException) {
            return ((YepException) exception).getError();
        }
        return null;
    }

    public static Skill findSkill(List<Skill> skills, String id) {
        if (skills == null || id == null) return null;
        for (Skill skill : skills) {
            if (id.equals(skill.getId())) return skill;
        }
        return null;
    }

    public static void saveUserInfo(final Context context, final Account account, final User user) {
        final AccountManager am = AccountManager.get(context);
        if (!TextUtils.equals(user.getId(), am.getUserData(account, USER_DATA_ID))) return;
        final Bundle userData = new Bundle();
        writeUserToUserData(user, userData);
        for (String key : userData.keySet()) {
            am.setUserData(account, key, userData.getString(key));
        }
    }

    public static Location getCachedLocation(Context context) {
        Location location = null;
        try {
            final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            try {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (SecurityException ignore) {

            }
            if (location != null) return location;
            try {
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } catch (SecurityException ignore) {

            }
        } catch (Exception ignore) {
        }
        return location;
    }

    public static void openSettings(Context context, Account account) {
        final Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsDetailsFragment.class.getName());
        final Bundle args = new Bundle();
        args.putInt(EXTRA_RESID, R.xml.pref_general);
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_ARGUMENTS, args);
        intent.putExtra(EXTRA_ACCOUNT, account);
        context.startActivity(intent);
    }


    public static String getTableName(final Uri uri) {
        return getTableName(getTableId(uri));
    }

    public static String getTableName(final int id) {
        return TABLE_NAMES.get(id);
    }

    public static int getTableId(final Uri uri) {
        return DATABASE_URI_MATCHER.match(uri);
    }

    public static long getTime(final Date date) {
        if (date == null) return 0;
        return date.getTime();
    }

    public static BitmapDrawable getMetadataBitmap(final Resources res, final FileAttachment.ImageMetadata metadata) {
        final byte[] bytes;
        try {
            bytes = Base64.decode(metadata.getBlurredThumbnail(), Base64.DEFAULT);
        } catch (IllegalArgumentException e) {
            return null;
        }
        final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        if (bitmap == null) return null;
        return new BitmapDrawable(res, bitmap);
    }

    public static String getConversationName(final Conversation conversation) {
        final String recipientType = conversation.getRecipientType();
        if (Message.RecipientType.CIRCLE.equals(recipientType)) {
            final Circle circle = conversation.getCircle();
            if (circle == null || circle.getTopic() == null || TextUtils.isEmpty(circle.getTopic().getBody())) {
                return "Circle";
            } else {
                return circle.getTopic().getBody();
            }
        } else if (Message.RecipientType.USER.equals(recipientType)) {
            final User user = conversation.getUser();
            if (user == null) return "User";
            return getDisplayName(user);
        }
        throw new UnsupportedOperationException("Unknown recipientType " + recipientType);
    }

    public static String getDisplayName(@NonNull final User user) {
        if (!TextUtils.isEmpty(user.getNickname())) return user.getNickname();
        else if (!TextUtils.isEmpty(user.getContactName())) return user.getContactName();
        else return user.getUsername();
    }

    public static String getAccountId(final Context context, final Account account) {
        if (account == null) return null;
        final AccountManager am = AccountManager.get(context);
        return am.getUserData(account, USER_DATA_ID);
    }

    @Nullable
    public static <T> ArrayList<T> arrayListFrom(@Nullable final List<T> list) {
        if (list == null) return null;
        return new ArrayList<>(list);
    }

    public static String getDistanceString(final float distanceMeters) {
        if (distanceMeters < 1000) {
            return String.format(Locale.US, "%.0f m", distanceMeters);
        }
        return String.format(Locale.US, "%.1f km", distanceMeters / 1000f);
    }

    public static <T> Object findFieldOfTypes(T obj, Class<? extends T> cls, Class<?>... checkTypes) {
        labelField:
        for (Field field : cls.getDeclaredFields()) {
            field.setAccessible(true);
            final Object fieldObj;
            try {
                fieldObj = field.get(obj);
            } catch (Exception ignore) {
                continue;
            }
            if (fieldObj != null) {
                final Class<?> type = fieldObj.getClass();
                for (Class<?> checkType : checkTypes) {
                    if (!checkType.isAssignableFrom(type)) continue labelField;
                }
                return fieldObj;
            }
        }
        return null;
    }

    public static String getDisplayName(final Skill skill) {
        return TextUtils.isEmpty(skill.getNameString()) ? skill.getName() : skill.getNameString();
    }

    @SuppressWarnings("SuspiciousSystemArraycopy")
    public static <T extends Parcelable> T[] newParcelableArray(Parcelable[] array, Parcelable.Creator<T> creator) {
        if (array == null) return null;
        final T[] result = creator.newArray(array.length);
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

    public static <T> List<T> emptyIfNull(final List<T> list) {
        if (list != null) return list;
        return Collections.emptyList();
    }

    public static boolean hasSkill(final User user, final Skill skill) {
        final List<Skill> learning = user.getLearningSkills();
        final List<Skill> mastered = user.getMasterSkills();
        if (learning != null && learning.contains(skill)) {
            return true;
        }
        if (mastered != null && mastered.contains(skill)) {
            return true;
        }
        return false;
    }

    public static String getUserLink(final User user) {
        return "http://soyep.com/" + user.getUsername();
    }

    public static String getConversationAvatarUrl(final Conversation conversation) {
        final String recipientType = conversation.getRecipientType();
        if (Message.RecipientType.CIRCLE.equals(recipientType)) {
            final Circle circle = conversation.getCircle();
            if (circle != null) {
                final Topic topic = circle.getTopic();
                if (topic != null) {
                    return getAttachmentThumb(topic.getAttachments(), topic.getAttachmentKind());
                }
            }
            return null;
        } else if (Message.RecipientType.USER.equals(recipientType)) {
            return conversation.getUser().getAvatarUrl();
        }
        throw new UnsupportedOperationException("Unknown recipientType " + recipientType);
    }

    private static String getAttachmentThumb(final List<Attachment> attachments, final String attachmentKind) {
        if (attachments == null || attachmentKind == null) return null;
        for (final Attachment attachment : attachments) {
            if (attachment instanceof FileAttachment) {
                if (Attachment.Kind.IMAGE.equals(attachmentKind)) {
                    return ((FileAttachment) attachment).getFile().getUrl();
                }
            }
        }
        return null;
    }

    public static String emptyIfNull(final CharSequence text) {
        if (text == null) return "";
        return String.valueOf(text);
    }

    public static String getDefaultAvatarUrl() {
        return Uri.fromParts(ContentResolver.SCHEME_ANDROID_RESOURCE,
                "//" + BuildConfig.APPLICATION_ID + "/" + R.drawable.ic_profile_image_default, null).toString();
    }

    public static String getFilename(File file, String mimeType) {
        final String name = file.getName();
        final int dotIndex = name.indexOf(".");
        final MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String extension = mimeTypeMap.getExtensionFromMimeType(mimeType);
        if (extension == null) {
            extension = MediaType.parse(mimeType).subtype();
        }
        if (dotIndex < 0) {
            return name + "." + extension;
        }
        return name.substring(0, dotIndex + 1) + extension;
    }

    public static Bitmap getMarkerBitmap(final Context context) {
        final Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_map_marker);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        final Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap;
    }

    public static String generateRandomId(int length) {
        final byte[] buf = new byte[length];
        sRandom.nextBytes(buf);
        return ByteString.of(buf).hex();
    }
}
