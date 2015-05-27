/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import catchla.yep.Constants;
import catchla.yep.model.User;

/**
 * Created by mariotaku on 15/5/5.
 */
public class Utils implements Constants {

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
}
