package catchla.yep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.fragment.DribbbleShotsFragment;
import catchla.yep.fragment.GithubUserInfoFragment;
import catchla.yep.fragment.InstagramMediaFragment;
import catchla.yep.model.Provider;

/**
 * Created by mariotaku on 15/6/3.
 */
public class ProviderContentActivity extends SwipeBackContentActivity implements Constants {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_content);
        final Intent intent = getIntent();
        final String providerName = intent.getStringExtra(EXTRA_PROVIDER_NAME);
        final Fragment fragment;
        if (Provider.PROVIDER_DRIBBBLE.equals(providerName)) {
            fragment = Fragment.instantiate(this, DribbbleShotsFragment.class.getName());
        } else if (Provider.PROVIDER_GITHUB.equals(providerName)) {
            fragment = Fragment.instantiate(this, GithubUserInfoFragment.class.getName());
        } else if (Provider.PROVIDER_INSTAGRAM.equals(providerName)) {
            fragment = Fragment.instantiate(this, InstagramMediaFragment.class.getName());
        } else {
            finish();
            return;
        }
        final Bundle args = new Bundle();
        args.putString(EXTRA_USER, intent.getStringExtra(EXTRA_USER));
        fragment.setArguments(args);
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_content, fragment);
        ft.commit();
    }


}
