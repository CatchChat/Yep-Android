/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep;

/**
 * Created by mariotaku on 15/4/30.
 */
public interface Constants {
    String YEP_DATABASE_NAME = "yep_data.db";
    int YEP_DATABASE_VERSION = 1;

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
    String USER_DATA_PHONE_NUMBER = "phone_number";
    String USER_DATA_COUNTRY_CODE = "country_code";
    String USER_DATA_INTRODUCTION = "introduction";
    String USER_DATA_MASTER_SKILLS = "master_skills";
    String USER_DATA_LEARNING_SKILLS = "learning_skills";
    String USER_DATA_PROVIDERS = "providers";

    String EXTRA_RESID = "resid";
    String EXTRA_TOKEN = "token";
    String EXTRA_USER = "user";
    String EXTRA_CONVERSATION = "conversation";
    String EXTRA_SKILL = "skill";
    String EXTRA_READ_CACHE = "read_cache";
    String EXTRA_LEARNING = "learning";
    String EXTRA_MASTER = "master";
    String EXTRA_PROVIDER_NAME = "provider_name";
    String EXTRA_SKILLS = "skills";
}
