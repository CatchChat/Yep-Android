/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.adapter.TabsAdapter;
import catchla.yep.fragment.ChatsListFragment;
import catchla.yep.fragment.DiscoverFragment;
import catchla.yep.fragment.FriendsListFragment;
import catchla.yep.fragment.TopicsListFragment;
import catchla.yep.fragment.iface.IActionButtonSupportFragment;
import catchla.yep.fragment.iface.RefreshScrollTopInterface;
import catchla.yep.menu.HomeMenuActionProvider;
import catchla.yep.util.ThemeUtils;
import catchla.yep.util.Utils;
import catchla.yep.view.TabPagerIndicator;
import catchla.yep.view.TintedStatusFrameLayout;
import catchla.yep.view.iface.PagerIndicator;

/**
 * Created by mariotaku on 15/4/29.
 */
public class HomeActivity extends AppCompatActivity implements Constants, ViewPager.OnPageChangeListener, View.OnClickListener, PagerIndicator.TabListener {
    private ViewPager mViewPager;
    private TabsAdapter mAdapter;
    private FloatingActionButton mActionButton;
    private TabPagerIndicator mPagerIndicator;
    private TintedStatusFrameLayout mMainContent;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mMainContent = (TintedStatusFrameLayout) findViewById(R.id.main_content);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mActionButton = (FloatingActionButton) findViewById(R.id.action_button);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        final HomeMenuActionProvider provider = new HomeMenuActionProvider(actionBar.getThemedContext());
        final Account account = Utils.getCurrentAccount(this);
        provider.setAccount(account);
        provider.setOnActionListener(new HomeMenuActionProvider.OnActionListener() {
            @Override
            public void onProfileClick() {
                final Intent intent = new Intent(HomeActivity.this, UserActivity.class);
                intent.putExtra(EXTRA_ACCOUNT, account);
                startActivity(intent);
            }

            @Override
            public void onActionClick(HomeMenuActionProvider.Action action) {
                switch (action.id) {
                    case R.id.settings: {
                        Utils.openSettings(HomeActivity.this);
                        break;
                    }
                    case R.id.about: {
                        startActivity(new Intent(HomeActivity.this, AboutActivity.class));
                        break;
                    }
                }
            }
        });
        MenuItemCompat.setActionProvider(menu.findItem(R.id.menu), provider);
        return true;
    }

    @Override
    protected void onDestroy() {
//        stopService(new Intent(this, FayeService.class));
        mViewPager.removeOnPageChangeListener(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.layout_home_tabs);
        final int primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0);
        actionBar.setBackgroundDrawable(ThemeUtils.getActionBarBackground(primaryColor, true));
        mPagerIndicator = (TabPagerIndicator) actionBar.getCustomView().findViewById(R.id.pager_indicator);
        setContentView(R.layout.activity_home);
        final Toolbar toolbar = (Toolbar) getWindow().findViewById(android.support.v7.appcompat.R.id.action_bar);
        toolbar.setContentInsetsRelative(getResources().getDimensionPixelSize(R.dimen.element_spacing_normal), 0);
        mAdapter = new TabsAdapter(actionBar.getThemedContext(), getSupportFragmentManager());
        mAdapter.setTabListener(this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(this);
        mMainContent.setDrawColor(true);
        mMainContent.setDrawShadow(false);
        mMainContent.setColor(primaryColor);
        mActionButton.setOnClickListener(this);

        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_ACCOUNT, getAccount());

        mAdapter.addTab(ChatsListFragment.class, getString(R.string.tab_title_chats), R.drawable.ic_action_chat, args);
        mAdapter.addTab(FriendsListFragment.class, getString(R.string.tab_title_friends), R.drawable.ic_action_contact, args);
        mAdapter.addTab(TopicsListFragment.class, getString(R.string.topics), R.drawable.ic_action_feeds, args);
        mAdapter.addTab(DiscoverFragment.class, getString(R.string.tab_title_explore), R.drawable.ic_action_explore, args);
        mPagerIndicator.setViewPager(mViewPager);
        mPagerIndicator.updateAppearance();

//        startService(new Intent(this, FayeService.class));

        updateActionButton();
    }

    private Account getAccount() {
        return Utils.getCurrentAccount(this);
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

    }

    @Override
    public void onPageReselected(final int position) {
        final Fragment fragment = getCurrentFragment();
        if (fragment instanceof RefreshScrollTopInterface) {
            ((RefreshScrollTopInterface) fragment).scrollToStart();
        }
    }

    @Override
    public void onPageSelected(final int position) {
        updateActionButton();
    }

    @Override
    public boolean onTabLongClick(final int position) {
        return false;
    }

    private void updateActionButton() {
        final Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof IActionButtonSupportFragment) {
            mActionButton.setImageResource(((IActionButtonSupportFragment) currentFragment).getActionIcon());
            mActionButton.show();
        } else {
            mActionButton.hide();
        }
    }

    private Fragment getCurrentFragment() {
        return (Fragment) mAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
    }

    @Override
    public void onPageScrollStateChanged(final int state) {

    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.action_button: {
                final Fragment fragment = getCurrentFragment();
                if (fragment instanceof IActionButtonSupportFragment) {
                    ((IActionButtonSupportFragment) fragment).onActionPerformed();
                }
                break;
            }
        }
    }
}
