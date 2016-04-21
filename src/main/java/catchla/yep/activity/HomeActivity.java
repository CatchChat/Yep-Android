/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.activity.iface.IAccountActivity;
import catchla.yep.activity.iface.IControlBarActivity;
import catchla.yep.adapter.TabsAdapter;
import catchla.yep.fragment.ConversationsListFragment;
import catchla.yep.fragment.DiscoverFragment;
import catchla.yep.fragment.FloatingActionMenuFragment;
import catchla.yep.fragment.FriendsListFragment;
import catchla.yep.fragment.SettingsDetailsFragment;
import catchla.yep.fragment.TopicsListFragment;
import catchla.yep.fragment.iface.IActionButtonSupportFragment;
import catchla.yep.fragment.iface.RefreshScrollTopInterface;
import catchla.yep.menu.HomeMenuActionProvider;
import catchla.yep.service.MessageService;
import catchla.yep.util.ThemeUtils;
import catchla.yep.util.Utils;
import catchla.yep.view.FloatingActionMenu;
import catchla.yep.view.TabPagerIndicator;
import catchla.yep.view.TintedStatusFrameLayout;
import catchla.yep.view.iface.PagerIndicator;

/**
 * Created by mariotaku on 15/4/29.
 */
public class HomeActivity extends AppCompatActivity implements Constants, IAccountActivity,
        ViewPager.OnPageChangeListener, View.OnClickListener, PagerIndicator.TabListener,
        IControlBarActivity {
    private ViewPager mViewPager;
    private TabsAdapter mAdapter;
    private FloatingActionButton mFloatingActionButton;
    private FloatingActionMenu mFloatingActionMenu;
    private TabPagerIndicator mPagerIndicator;
    private TintedStatusFrameLayout mMainContent;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mMainContent = (TintedStatusFrameLayout) findViewById(R.id.main_content);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        mFloatingActionMenu = (FloatingActionMenu) findViewById(R.id.floating_action_menur);
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
                        Utils.openSettings(HomeActivity.this, getAccount());
                        break;
                    }
                    case R.id.about: {
                        startActivity(new Intent(HomeActivity.this, AboutActivity.class));
                        break;
                    }
                    case R.id.development: {
                        final Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                        intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, SettingsDetailsFragment.class.getName());
                        final Bundle args = new Bundle();
                        args.putInt(EXTRA_RESID, R.xml.pref_dev);
                        intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT_ARGUMENTS, args);
                        startActivity(intent);
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
        mFloatingActionButton.setOnClickListener(this);

        final Bundle args = new Bundle();
        args.putBoolean(EXTRA_CACHING_ENABLED, true);
        final Account account = getAccount();
        args.putParcelable(EXTRA_ACCOUNT, account);

        mAdapter.addTab(ConversationsListFragment.class, getString(R.string.tab_title_chats), R.drawable.ic_action_chat, args);
        mAdapter.addTab(FriendsListFragment.class, getString(R.string.tab_title_friends), R.drawable.ic_action_contact, args);
        mAdapter.addTab(TopicsListFragment.class, getString(R.string.topics), R.drawable.ic_action_feeds, args);
        mAdapter.addTab(DiscoverFragment.class, getString(R.string.tab_title_explore), R.drawable.ic_action_explore, args);
        mPagerIndicator.setViewPager(mViewPager);
        mPagerIndicator.updateAppearance();

        updateActionButton();

        final Intent intent = new Intent(this, MessageService.class);
        intent.setAction(MessageService.ACTION_REFRESH_FRIENDSHIPS);
        intent.putExtra(EXTRA_ACCOUNT, account);
        startService(intent);
    }

    @Override
    public Account getAccount() {
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
            mFloatingActionButton.setImageResource(((IActionButtonSupportFragment) currentFragment).getActionIcon());
            mFloatingActionButton.show();
            final Class<? extends FloatingActionMenuFragment> actionMenuFragmentCls =
                    ((IActionButtonSupportFragment) currentFragment).getActionMenuFragment();
            final FragmentManager fm = getSupportFragmentManager();
            final FragmentTransaction ft = fm.beginTransaction();
            if (actionMenuFragmentCls != null) {
                mFloatingActionMenu.setVisibility(View.VISIBLE);
                FloatingActionMenuFragment actionMenuFragment = (FloatingActionMenuFragment)
                        Fragment.instantiate(this, actionMenuFragmentCls.getName());
                actionMenuFragment.setBelongsTo(currentFragment);
                ft.replace(mFloatingActionMenu.getId(), actionMenuFragment);
            } else {
                final Fragment currentMenuFragment = fm.findFragmentById(mFloatingActionMenu.getId());
                if (currentMenuFragment != null) {
                    ft.remove(currentMenuFragment);
                }
                mFloatingActionMenu.setVisibility(View.GONE);
            }
            ft.commit();
        } else {
            mFloatingActionButton.hide();
            mFloatingActionMenu.setVisibility(View.GONE);
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
            case R.id.floating_action_button: {
                final Fragment fragment = getCurrentFragment();
                if (fragment instanceof IActionButtonSupportFragment) {
                    ((IActionButtonSupportFragment) fragment).onActionPerformed();
                }
                break;
            }
        }
    }

    @Override
    public void setControlBarOffset(final float offset) {

    }

    @Override
    public void setControlBarVisibleAnimate(final boolean visible) {
        final Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof IActionButtonSupportFragment) {
            if (visible) {
                mFloatingActionButton.show();
                if (((IActionButtonSupportFragment) currentFragment).getActionMenuFragment() != null) {
                    mFloatingActionMenu.show();
                } else {
                    mFloatingActionMenu.hide();
                }
            } else {
                mFloatingActionButton.hide();
                mFloatingActionMenu.hide();
            }
        } else {
            mFloatingActionButton.hide();
            mFloatingActionMenu.hide();
        }
    }

    @Override
    public float getControlBarOffset() {
        return 0;
    }

    @Override
    public int getControlBarHeight() {
        return 0;
    }

    @Override
    public void notifyControlBarOffsetChanged() {

    }

    @Override
    public void registerControlBarOffsetListener(final ControlBarOffsetListener listener) {

    }

    @Override
    public void unregisterControlBarOffsetListener(final ControlBarOffsetListener listener) {

    }
}
