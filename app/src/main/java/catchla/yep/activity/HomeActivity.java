/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.adapter.TabsAdapter;
import catchla.yep.fragment.ChatsListFragment;
import catchla.yep.fragment.DiscoverFragment;
import catchla.yep.fragment.FriendsListFragment;
import catchla.yep.fragment.SettingsDetailsFragment;
import catchla.yep.menu.HomeMenuActionProvider;
import catchla.yep.util.ThemeUtils;
import catchla.yep.util.Utils;
import catchla.yep.view.TabPagerIndicator;
import catchla.yep.view.TintedStatusFrameLayout;

/**
 * Created by mariotaku on 15/4/29.
 */
public class HomeActivity extends AppCompatActivity implements Constants {
    private ViewPager mViewPager;
    private TabsAdapter mAdapter;
    private TabPagerIndicator mPagerIndicator;
    private TintedStatusFrameLayout mMainContent;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mMainContent = (TintedStatusFrameLayout) findViewById(R.id.main_content);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        final HomeMenuActionProvider provider = new HomeMenuActionProvider(actionBar.getThemedContext());
        provider.setAccount(Utils.getCurrentAccount(this));
        provider.setOnActionListener(new HomeMenuActionProvider.OnActionListener() {
            @Override
            public void onProfileClick() {
                startActivity(new Intent(HomeActivity.this, UserActivity.class));
            }

            @Override
            public void onActionClick(HomeMenuActionProvider.Action action) {
                switch (action.id) {
                    case R.id.settings: {
                        final Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                        intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, SettingsDetailsFragment.class.getName());
                        final Bundle args = new Bundle();
                        args.putInt(EXTRA_RESID, R.xml.pref_general);
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
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mMainContent.setDrawColor(true);
        mMainContent.setDrawShadow(false);
        mMainContent.setColor(primaryColor);
        mAdapter.addTab(ChatsListFragment.class, getString(R.string.tab_title_chats), R.drawable.ic_action_chat, null);
        mAdapter.addTab(FriendsListFragment.class, getString(R.string.tab_title_friends), R.drawable.ic_action_contact, null);
        mAdapter.addTab(DiscoverFragment.class, getString(R.string.tab_title_explore), R.drawable.ic_action_explore, null);
        mPagerIndicator.setViewPager(mViewPager);
        mPagerIndicator.updateAppearance();
    }

}
