package catchla.yep.extension

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import catchla.yep.Constants
import catchla.yep.model.Provider
import catchla.yep.model.Skill
import catchla.yep.model.User
import catchla.yep.util.JsonSerializer

/**
 * Created by mariotaku on 16/8/23.
 */

fun Account.getUser(context: Context): User {
    val am = AccountManager.get(context)
    val user = User()
    user.id = am.getUserData(this, Constants.USER_DATA_ID)
    user.accountId = user.id
    user.nickname = am.getUserData(this, Constants.USER_DATA_NICKNAME)
    user.avatarUrl = am.getUserData(this, Constants.USER_DATA_AVATAR)
    user.phoneCode = am.getUserData(this, Constants.USER_DATA_COUNTRY_CODE)
    user.mobile = am.getUserData(this, Constants.USER_DATA_PHONE_NUMBER)
    user.introduction = am.getUserData(this, Constants.USER_DATA_INTRODUCTION)
    user.username = am.getUserData(this, Constants.USER_DATA_USERNAME)
    user.websiteUrl = am.getUserData(this, Constants.USER_DATA_WEBSITE)
    val learningJson = am.getUserData(this, Constants.USER_DATA_LEARNING_SKILLS)
    user.learningSkills = JsonSerializer.parseList(learningJson, Skill::class.java)
    val masterJson = am.getUserData(this, Constants.USER_DATA_MASTER_SKILLS)
    user.masterSkills = JsonSerializer.parseList(masterJson, Skill::class.java)
    val providersJson = am.getUserData(this, Constants.USER_DATA_PROVIDERS)
    user.providers = JsonSerializer.parseList(providersJson, Provider::class.java)
    user.badge = User.Badge.parse(am.getUserData(this, Constants.USER_DATA_BADGE))
    return user
}