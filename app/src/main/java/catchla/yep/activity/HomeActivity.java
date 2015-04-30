/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import catchla.yep.R;
import catchla.yep.fragment.ChatsListFragment;
import catchla.yep.fragment.FriendsListFragment;
import catchla.yep.fragment.ExploreFragment;
import catchla.yep.fragment.MyProfileFragment;

/**
 * Created by mariotaku on 15/4/29.
 */
public class HomeActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private HomeTabsAdapter mAdapter;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAdapter = new HomeTabsAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mAdapter.addTab(ChatsListFragment.class, getString(R.string.tab_title_chats), 0, null);
        mAdapter.addTab(FriendsListFragment.class, getString(R.string.tab_title_friends), 0, null);
        mAdapter.addTab(ExploreFragment.class, getString(R.string.tab_title_explore), 0, null);
        mAdapter.addTab(MyProfileFragment.class, getString(R.string.tab_title_my_profile), 0, null);
    }

    private class HomeTabsAdapter extends FragmentStatePagerAdapter {
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
