package catchla.yep.extension

import android.accounts.Account
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import catchla.yep.Constants.EXTRA_ACCOUNT

/**
 * Created by mariotaku on 16/8/15.
 */

val Activity.account: Account
    get() = intent.getParcelableExtra(EXTRA_ACCOUNT)

val Activity.accountOptional: Account?
    get() = intent.getParcelableExtra(EXTRA_ACCOUNT)

var AppCompatActivity.subtitle: CharSequence?
    get() = supportActionBar?.subtitle
    set(value) {
        supportActionBar?.subtitle = value
    }