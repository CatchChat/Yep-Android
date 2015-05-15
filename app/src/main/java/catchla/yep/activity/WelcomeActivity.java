/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.fragment.ChatsListFragment;
import catchla.yep.fragment.ExploreFragment;
import catchla.yep.fragment.FriendsListFragment;
import catchla.yep.util.ThemeUtils;
import catchla.yep.view.TabPagerIndicator;
import catchla.yep.view.TintedStatusFrameLayout;
import catchla.yep.view.iface.PagerIndicator;

public class WelcomeActivity extends AppCompatActivity implements Constants {
    private ViewPager mViewPager;
    private HomeTabsAdapter mAdapter;
    private TabPagerIndicator mPagerIndicator;
    private TintedStatusFrameLayout mMainContent;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mMainContent = (TintedStatusFrameLayout) findViewById(R.id.main_content);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.layout_welcome_tabs);
        final int primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0);
        actionBar.setBackgroundDrawable(ThemeUtils.getActionBarBackground(primaryColor, true));
        mPagerIndicator = (TabPagerIndicator) actionBar.getCustomView().findViewById(R.id.pager_indicator);
        setContentView(R.layout.activity_welcome);
        final Toolbar toolbar = (Toolbar) getWindow().findViewById(android.support.v7.appcompat.R.id.action_bar);
        toolbar.setContentInsetsRelative(0, 0);

        mAdapter = new HomeTabsAdapter(actionBar.getThemedContext(), getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mMainContent.setDrawColor(true);
        mMainContent.setDrawShadow(false);
        mMainContent.setColor(primaryColor);
        mAdapter.addTab(ChatsListFragment.class, getString(R.string.tab_title_chats), R.drawable.ic_action_chat, null);
        mAdapter.addTab(FriendsListFragment.class, getString(R.string.tab_title_friends), R.drawable.ic_action_contact, null);
        mAdapter.addTab(ExploreFragment.class, getString(R.string.tab_title_explore), R.drawable.ic_action_explore, null);
        mPagerIndicator.setViewPager(mViewPager);
        mPagerIndicator.updateAppearance();
    }

    private class HomeTabsAdapter extends FragmentStatePagerAdapter implements PagerIndicator.TabProvider {
        private final Context mContext;

        public HomeTabsAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        private List<TabSpec> mTabs = new ArrayList<>();

        @Override
        public Fragment getItem(int position) {
            final TabSpec spec = mTabs.get(position);
            return Fragment.instantiate(mContext, spec.cls.getName(), spec.args);
        }

        public void addTab(Class<? extends Fragment> cls, CharSequence title, int icon, Bundle args) {
            mTabs.add(new TabSpec(cls, title, icon, args));
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).title;
        }

        @Override
        public Drawable getPageIcon(int position) {
            return ContextCompat.getDrawable(mContext, mTabs.get(position).icon);
        }
    }

    class TabSpec {
        Class<? extends Fragment> cls;
        CharSequence title;
        int icon;

        TabSpec(Class<? extends Fragment> cls, CharSequence title, int icon, Bundle args) {
            this.cls = cls;
            this.title = title;
            this.icon = icon;
            this.args = args;
        }

        Bundle args;
    }
}
