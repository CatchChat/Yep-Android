/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep;

/**
 * Created by mariotaku on 15/4/30.
 */
public interface Constants {

    String LOGTAG = "Yep";

    String SHARED_PREFERENCES_NAME = "preferences";

    String KEY_CURRENT_ACCOUNT = "current_account";

    String ACCOUNT_TYPE = "catchla.yep.account";
    String ACCOUNT_TYPE_PREFIX = ACCOUNT_TYPE + ":";
    String AUTH_TOKEN_TYPE = ACCOUNT_TYPE_PREFIX + "auth_token";

    String USER_DATA_ID = "id";
    String USER_DATA_AVATAR = "avatar";
    String USER_DATA_NICKNAME = "nickname";
    String USER_DATA_USERNAME = "username";

    String EXTRA_RESID = "resid";
    String EXTRA_TOKEN = "token";
    String EXTRA_USER = "user";
}
