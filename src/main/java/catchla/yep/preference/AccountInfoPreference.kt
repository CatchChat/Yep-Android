package catchla.yep.preference

import android.accounts.Account
import android.content.Context
import android.content.Intent
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceViewHolder
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.activity.ProfileEditorActivity
import catchla.yep.model.User
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.Utils
import catchla.yep.util.dagger.GeneralComponentHelper
import javax.inject.Inject

/**
 * Created by mariotaku on 15/5/11.
 */
class AccountInfoPreference @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.preferenceStyle) :
        Preference(context, attrs, defStyleAttr), Constants {

    private val mAccount: Account?
    private val mAccountUser: User?
    @Inject
    lateinit internal var mImageLoader: ImageLoaderWrapper

    init {
        GeneralComponentHelper.build(context).inject(this)
        layoutResource = R.layout.layout_preference_account_info
        mAccount = Utils.getCurrentAccount(context)
        mAccountUser = Utils.getCurrentAccountUser(context)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        if (mAccount != null && mAccountUser != null) {
            val profileImageView = holder.findViewById(R.id.account_profile_image) as ImageView
            val nameView = holder.findViewById(R.id.account_name) as TextView
            val introductionView = holder.findViewById(R.id.account_introduction) as TextView
            mImageLoader.displayProfileImage(mAccountUser.avatarThumbUrl, profileImageView)
            nameView.text = mAccountUser.nickname
            introductionView.text = mAccountUser.introduction
        }
    }

    override fun onClick() {
        val context = context
        val intent = Intent(context, ProfileEditorActivity::class.java)
        intent.putExtra(Constants.EXTRA_ACCOUNT, mAccount)
        context.startActivity(intent)
    }
}
