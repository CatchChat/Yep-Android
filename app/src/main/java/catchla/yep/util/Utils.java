/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.squareup.otto.Bus;

import org.mariotaku.restfu.annotation.method.POST;
import org.mariotaku.restfu.http.RestHttpClient;
import org.mariotaku.restfu.http.RestHttpRequest;
import org.mariotaku.restfu.http.RestHttpResponse;
import org.mariotaku.restfu.http.mime.FileTypedData;
import org.mariotaku.restfu.http.mime.MultipartTypedBody;
import org.mariotaku.restfu.http.mime.StringTypedData;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.activity.SettingsActivity;
import catchla.yep.fragment.SettingsDetailsFragment;
import catchla.yep.model.Provider;
import catchla.yep.model.S3UploadToken;
import catchla.yep.model.Skill;
import catchla.yep.model.User;
import catchla.yep.provider.YepDataStore.Conversations;
import catchla.yep.provider.YepDataStore.Friendships;
import catchla.yep.provider.YepDataStore.Messages;

/**
 * Created by mariotaku on 15/5/5.
 */
public class Utils implements Constants {

    private static final UriMatcher DATABASE_URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final SparseArray<String> TABLE_NAMES = new SparseArray<>();

    static {
        DATABASE_URI_MATCHER.addURI(AUTHORITY_YEP, Friendships.CONTENT_PATH, TABLE_ID_FRIENDSHIPS);
        DATABASE_URI_MATCHER.addURI(AUTHORITY_YEP, Messages.CONTENT_PATH, TABLE_ID_MESSAGES);
        DATABASE_URI_MATCHER.addURI(AUTHORITY_YEP, Conversations.CONTENT_PATH, TABLE_ID_CONVERSATIONS);
        TABLE_NAMES.append(TABLE_ID_FRIENDSHIPS, Friendships.TABLE_NAME);
        TABLE_NAMES.append(TABLE_ID_MESSAGES, Messages.TABLE_NAME);
        TABLE_NAMES.append(TABLE_ID_CONVERSATIONS, Conversations.TABLE_NAME);
    }

    public static final Pattern PATTERN_XML_RESOURCE_IDENTIFIER = Pattern.compile("res/xml/([\\w_]+)\\.xml");

    public static final Pattern PATTERN_RESOURCE_IDENTIFIER = Pattern.compile("@([\\w_]+)/([\\w_]+)");
    private static Bus sMessageBus;

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
        final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
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
        return getAccountUser(context, getCurrentAccount(context));
    }


    @Nullable
    public static User getAccountUser(Context context, Account account) {
        if (account == null) return null;
        final AccountManager am = AccountManager.get(context);
        final User user = new User();
        user.setId(am.getUserData(account, USER_DATA_ID));
        user.setNickname(am.getUserData(account, USER_DATA_NICKNAME));
        user.setAvatarUrl(am.getUserData(account, USER_DATA_AVATAR));
        user.setPhoneCode(am.getUserData(account, USER_DATA_COUNTRY_CODE));
        user.setMobile(am.getUserData(account, USER_DATA_PHONE_NUMBER));
        user.setIntroduction(am.getUserData(account, USER_DATA_INTRODUCTION));
        final String learningJson = am.getUserData(account, USER_DATA_LEARNING_SKILLS);
        if (learningJson != null) {
            try {
                user.setLearningSkills(LoganSquare.parseList(learningJson, Skill.class));
            } catch (IOException ignore) {
            }
        }
        final String masterJson = am.getUserData(account, USER_DATA_MASTER_SKILLS);
        if (masterJson != null) {
            try {
                user.setMasterSkills(LoganSquare.parseList(masterJson, Skill.class));
            } catch (IOException ignore) {
            }
        }
        final String providersJson = am.getUserData(account, USER_DATA_PROVIDERS);
        if (providersJson != null) {
            try {
                user.setProviders(LoganSquare.parseList(providersJson, Provider.class));
            } catch (IOException ignore) {
            }
        }
        return user;
    }

    public static void setCurrentAccount(Context context, Account account) {
        final SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
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

    public static String formatSameDayTime(final Context context, final long timestamp) {
        if (context == null) return null;
        if (DateUtils.isToday(timestamp))
            //noinspection deprecation
            return DateUtils.formatDateTime(context, timestamp,
                    DateFormat.is24HourFormat(context) ? DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR
                            : DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_12HOUR);
        return DateUtils.formatDateTime(context, timestamp, DateUtils.FORMAT_SHOW_DATE);
    }

    public static Bus getMessageBus() {
        if (sMessageBus != null) return sMessageBus;
        return sMessageBus = new Bus();
    }

    public static void writeUserToUserData(final User user, final Bundle userData) {
        userData.putString(USER_DATA_ID, user.getId());
        userData.putString(USER_DATA_AVATAR, user.getAvatarUrl());
        userData.putString(USER_DATA_NICKNAME, user.getNickname());
        userData.putString(USER_DATA_USERNAME, user.getUsername());
        userData.putString(USER_DATA_PHONE_NUMBER, user.getMobile());
        userData.putString(USER_DATA_COUNTRY_CODE, user.getPhoneCode());
        userData.putString(USER_DATA_INTRODUCTION, user.getIntroduction());
        if (user.getLearningSkills() != null) {
            try {
                userData.putString(USER_DATA_LEARNING_SKILLS,
                        LoganSquare.serialize(user.getLearningSkills(), Skill.class));
            } catch (IOException ignore) {
            }
        }
        if (user.getMasterSkills() != null) {
            try {
                userData.putString(USER_DATA_MASTER_SKILLS,
                        LoganSquare.serialize(user.getMasterSkills(), Skill.class));
            } catch (IOException ignore) {
            }
        }
        if (user.getProviders() != null) {
            try {
                userData.putString(USER_DATA_PROVIDERS,
                        LoganSquare.serialize(user.getProviders(), Provider.class));
            } catch (IOException ignore) {
            }
        }
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

    public static View inflateAddSkillView(final Context context, final LayoutInflater inflater, final ViewGroup parent) {
        return inflater.inflate(R.layout.layout_skill_add_button, parent, false);
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

    public static RestHttpResponse uploadToS3(RestHttpClient client, S3UploadToken token, File file) throws IOException {
        return uploadToS3(client, token, new FileTypedData(file));
    }

    public static RestHttpResponse uploadToS3(RestHttpClient client, S3UploadToken token, FileTypedData file) throws IOException {
        final S3UploadToken.Options options = token.getOptions();
        final S3UploadToken.Policy policy = options.getPolicy();
        final RestHttpRequest.Builder builder = new RestHttpRequest.Builder();
        builder.method(POST.METHOD);
        builder.url(options.getUrl());
        final MultipartTypedBody body = new MultipartTypedBody();
        final Charset charset = Charset.forName("UTF-8");
        final S3UploadToken.Condition key = S3UploadToken.Policy.getCondition(policy, "key");
        final S3UploadToken.Condition acl = S3UploadToken.Policy.getCondition(policy, "acl");
        final S3UploadToken.Condition algorithm = S3UploadToken.Policy.getCondition(policy, "x-amz-algorithm");
        final S3UploadToken.Condition credential = S3UploadToken.Policy.getCondition(policy, "x-amz-credential");
        final S3UploadToken.Condition date = S3UploadToken.Policy.getCondition(policy, "x-amz-date");
        assert key != null && acl != null && algorithm != null && credential != null && date != null;
        body.add("key", new StringTypedData(key.getValue(), charset));
        body.add("acl", new StringTypedData(acl.getValue(), charset));
        body.add("X-Amz-Algorithm", new StringTypedData(algorithm.getValue(), charset));
        body.add("X-Amz-Signature", new StringTypedData(options.getSignature(), charset));
        body.add("X-Amz-Date", new StringTypedData(date.getValue(), charset));
        body.add("X-Amz-Credential", new StringTypedData(credential.getValue(), charset));
        body.add("Policy", new StringTypedData(options.getEncodedPolicy(), charset));
        body.add("file", file);
        builder.body(body);
        return client.execute(builder.build());
    }

    public static void openSettings(Context context) {
        final Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, SettingsDetailsFragment.class.getName());
        final Bundle args = new Bundle();
        args.putInt(EXTRA_RESID, R.xml.pref_general);
        intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT_ARGUMENTS, args);
        context.startActivity(intent);
    }


    public static String getTableName(final Uri uri) {
        return getTableName(getTableId(uri));
    }

    public static String getTableName(final int id) {
        return TABLE_NAMES.get(id);
    }

    private static int getTableId(final Uri uri) {
        return DATABASE_URI_MATCHER.match(uri);
    }
}
