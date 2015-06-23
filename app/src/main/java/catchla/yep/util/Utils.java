/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.squareup.otto.Bus;

import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.model.Provider;
import catchla.yep.model.Skill;
import catchla.yep.model.User;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created by mariotaku on 15/5/5.
 */
public class Utils implements Constants {

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


    @NonNull
    public static Realm getRealmForAccount(@NonNull Context context, @NonNull Account account) {
        return getRealmForAccountInternal(context, account, false);
    }


    public static Realm getRealmForAccountInternal(@NonNull Context context, @NonNull Account account, boolean migrated) {
        final File databaseDir = context.getFilesDir();
        final String databaseName = String.format(Locale.ROOT, "account_db_%s", account.name);
        try {
            return Realm.getInstance(databaseDir, databaseName);
        } catch (RealmMigrationNeededException e) {
            if (migrated) throw e;
            Realm.deleteRealmFile(context, databaseName);
            return getRealmForAccountInternal(context, account, true);
        }
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
                final List<Skill> learningSkills = LoganSquare.parseList(learningJson, Skill.class);
                if (learningSkills != null) {
                    final RealmList<Skill> list = new RealmList<>();
                    list.addAll(learningSkills);
                    user.setLearningSkills(list);
                }
            } catch (IOException ignore) {
            }
        }
        final String masterJson = am.getUserData(account, USER_DATA_MASTER_SKILLS);
        if (masterJson != null) {
            try {
                final List<Skill> masterSkills = LoganSquare.parseList(masterJson, Skill.class);
                if (masterSkills != null) {
                    final RealmList<Skill> list = new RealmList<>();
                    list.addAll(masterSkills);
                    user.setMasterSkills(list);
                }
            } catch (IOException ignore) {
            }
        }
        final String providersJson = am.getUserData(account, USER_DATA_PROVIDERS);
        if (providersJson != null) {
            try {
                final List<Provider> providers = LoganSquare.parseList(providersJson, Provider.class);
                if (providers != null) {
                    final RealmList<Provider> list = new RealmList<>();
                    list.addAll(providers);
                    user.setProviders(list);
                }
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
}
