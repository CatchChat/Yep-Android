package catchla.yep.extension

import android.accounts.Account
import android.support.v4.app.Fragment
import catchla.yep.Constants.EXTRA_ACCOUNT

/**
 * Created by mariotaku on 16/8/15.
 */

val Fragment.account: Account
    get() = arguments.getParcelable(EXTRA_ACCOUNT)

val Fragment.accountOptional: Account?
    get() = arguments.getParcelable(EXTRA_ACCOUNT)