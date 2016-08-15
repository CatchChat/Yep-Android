package catchla.yep.extension

import android.accounts.Account
import android.app.Activity
import catchla.yep.Constants.EXTRA_ACCOUNT

/**
 * Created by mariotaku on 16/8/15.
 */

val Activity.account: Account
    get() = intent.getParcelableExtra(EXTRA_ACCOUNT)

val Activity.accountOptional: Account?
    get() = intent.getParcelableExtra(EXTRA_ACCOUNT)