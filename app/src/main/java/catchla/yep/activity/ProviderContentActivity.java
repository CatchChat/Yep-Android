package catchla.yep.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.fragment.DribbbleShotsFragment;
import catchla.yep.fragment.GithubUserInfoFragment;
import catchla.yep.model.GithubUserInfo;
import catchla.yep.view.holder.GithubRepoItemViewHolder;
import catchla.yep.view.holder.GithubUserHeaderViewHolder;

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
        if ("dribbble".equals(providerName)) {
            fragment = Fragment.instantiate(this, DribbbleShotsFragment.class.getName());
        } else if ("github".equals(providerName)) {
            fragment = Fragment.instantiate(this, GithubUserInfoFragment.class.getName());
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
