package catchla.yep.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import catchla.yep.R;
import catchla.yep.adapter.TabsAdapter;
import catchla.yep.view.HeaderDrawerLayout;
import catchla.yep.view.TabPagerIndicator;

/**
 * Created by mariotaku on 15/6/2.
 */
public class SkillActivity extends SwipeBackContentActivity implements HeaderDrawerLayout.DrawerCallback {

    private HeaderDrawerLayout mDrawerCallback;
    private ViewPager mViewPager;
    private TabPagerIndicator mPagerTab;
    private TabsAdapter mPagerAdapter;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mDrawerCallback = (HeaderDrawerLayout) findViewById(R.id.header_drawer);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerTab = (TabPagerIndicator) findViewById(R.id.pager_tab);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill);
        mPagerAdapter = new TabsAdapter(this, getSupportFragmentManager());
        mDrawerCallback.setDrawerCallback(this);
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public boolean canScroll(final float dy) {
        return false;
    }

    @Override
    public void cancelTouch() {

    }

    @Override
    public void fling(final float velocity) {

    }

    @Override
    public boolean isScrollContent(final float x, final float y) {
        return false;
    }

    @Override
    public void scrollBy(final float dy) {

    }

    @Override
    public boolean shouldLayoutHeaderBottom() {
        return false;
    }

    @Override
    public void topChanged(final int offset) {

    }
}
