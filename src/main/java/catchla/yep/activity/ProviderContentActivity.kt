package catchla.yep.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.fragment.DribbbleShotsFragment
import catchla.yep.fragment.GithubUserInfoFragment
import catchla.yep.fragment.InstagramMediaFragment
import catchla.yep.model.Provider

/**
 * Created by mariotaku on 15/6/3.
 */
class ProviderContentActivity : SwipeBackContentActivity(), Constants {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_content)
        val providerName = intent.getStringExtra(Constants.EXTRA_PROVIDER_NAME)
        val fragment: Fragment
        when (providerName) {
            Provider.PROVIDER_DRIBBBLE -> fragment = Fragment.instantiate(this, DribbbleShotsFragment::class.java.name)
            Provider.PROVIDER_GITHUB -> fragment = Fragment.instantiate(this, GithubUserInfoFragment::class.java.name)
            Provider.PROVIDER_INSTAGRAM -> fragment = Fragment.instantiate(this, InstagramMediaFragment::class.java.name)
            else -> {
                finish()
                return
            }
        }
        val args = Bundle()
        args.putParcelable(Constants.EXTRA_ACCOUNT, intent.getParcelableExtra(Constants.EXTRA_ACCOUNT))
        args.putParcelable(Constants.EXTRA_USER, intent.getParcelableExtra(Constants.EXTRA_USER))
        fragment.arguments = args
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(R.id.main_content, fragment)
        ft.commit()
    }


}
