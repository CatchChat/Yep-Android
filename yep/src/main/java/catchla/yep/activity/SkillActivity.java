package catchla.yep.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.adapter.TabsAdapter;
import catchla.yep.fragment.DiscoverFragment;
import catchla.yep.graphic.ActionBarDrawable;
import catchla.yep.model.Skill;
import catchla.yep.util.MathUtils;
import catchla.yep.util.ThemeUtils;
import catchla.yep.view.HeaderDrawerLayout;
import catchla.yep.view.HeaderSpaceLayout;
import catchla.yep.view.RatioFrameLayout;
import catchla.yep.view.TabPagerIndicator;
import catchla.yep.view.TintedStatusFrameLayout;
import catchla.yep.view.iface.IExtendedView;

/**
 * Created by mariotaku on 15/6/2.
 */
public class SkillActivity extends SwipeBackContentActivity implements Constants,
        HeaderDrawerLayout.DrawerCallback, IExtendedView.OnFitSystemWindowsListener {

    private TintedStatusFrameLayout mMainContent;
    private HeaderDrawerLayout mHeaderDrawerLayout;
    private ViewPager mViewPager;
    private TabPagerIndicator mPagerTab;
    private HeaderSpaceLayout mHeaderSpaceLayout;
    private ImageView mBannerImageView;
    private RatioFrameLayout mBannerContainer;

    private TabsAdapter mPagerAdapter;

    private ActionBarDrawable mActionBarBackground;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mMainContent = (TintedStatusFrameLayout) findViewById(R.id.main_content);
        mHeaderDrawerLayout = (HeaderDrawerLayout) findViewById(R.id.header_drawer);
        mHeaderSpaceLayout = (HeaderSpaceLayout) findViewById(R.id.header_space);
        mBannerImageView = (ImageView) findViewById(R.id.banner_image);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerTab = (TabPagerIndicator) findViewById(R.id.pager_tab);
        mBannerContainer = (RatioFrameLayout) findViewById(R.id.banner_container);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill);
        mPagerAdapter = new TabsAdapter(this, getSupportFragmentManager());
        mHeaderDrawerLayout.setDrawerCallback(this);
        mViewPager.setAdapter(mPagerAdapter);
        mPagerTab.setViewPager(mViewPager);
        mPagerTab.updateAppearance();

        mHeaderSpaceLayout.setWidthHeightRatio(0.64f);
        mBannerContainer.setWidthHeightRatio(0.64f);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        final int primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0);

        final Drawable shadow = ResourcesCompat.getDrawable(getResources(), R.drawable.shadow_user_banner_action_bar, null);

        mActionBarBackground = new ActionBarDrawable(shadow);
        actionBar.setBackgroundDrawable(mActionBarBackground);

        mMainContent.setDrawShadow(true);
        mMainContent.setDrawColor(true);
        mMainContent.setOnFitSystemWindowsListener(this);

        mMainContent.setShadowColor(0xA0000000);
        mActionBarBackground.setColor(primaryColor);
        mMainContent.setColor(primaryColor);

        final Intent intent = getIntent();
        final Skill skill = intent.getParcelableExtra(EXTRA_SKILL);
        displaySkill(skill);
        final Bundle masterArgs = new Bundle();
        masterArgs.putParcelable(EXTRA_ACCOUNT, getAccount());
        masterArgs.putStringArray(EXTRA_MASTER, new String[]{skill.getId()});
        final Bundle learningArgs = new Bundle();
        learningArgs.putParcelable(EXTRA_ACCOUNT, getAccount());
        learningArgs.putStringArray(EXTRA_LEARNING, new String[]{skill.getId()});
        mPagerAdapter.addTab(DiscoverFragment.class, getString(R.string.master), 0, masterArgs);
        mPagerAdapter.addTab(DiscoverFragment.class, getString(R.string.learning), 0, learningArgs);

        topChanged(0);
    }

    private void displaySkill(final Skill skill) {
        final String coverUrl = skill.getCoverUrl();
        if (!TextUtils.isEmpty(coverUrl)) {
            Glide.with(this).load(coverUrl).into(mBannerImageView);
        } else {
            Glide.clear(mBannerImageView);
        }
        setTitle(skill.getNameString());
    }

    @Override
    public boolean canScroll(final float dy) {
        final HeaderDrawerLayout.DrawerCallback callback = getCurrentCallback();
        if (callback == null) return false;
        return callback.canScroll(dy);
    }

    @Override
    public void cancelTouch() {
        final HeaderDrawerLayout.DrawerCallback callback = getCurrentCallback();
        if (callback == null) return;
        callback.cancelTouch();
    }

    @Override
    public void fling(final float velocity) {
        final HeaderDrawerLayout.DrawerCallback callback = getCurrentCallback();
        if (callback == null) return;
        callback.fling(velocity);
    }

    @Override
    public boolean isScrollContent(final float x, final float y) {
        final HeaderDrawerLayout.DrawerCallback callback = getCurrentCallback();
        if (callback == null) return false;
        return callback.isScrollContent(x, y);
    }

    @Override
    public void scrollBy(final float dy) {
        final HeaderDrawerLayout.DrawerCallback callback = getCurrentCallback();
        if (callback == null) return;
        callback.scrollBy(dy);
    }

    @Override
    public boolean shouldLayoutHeaderBottom() {
        return true;
    }

    public HeaderDrawerLayout.DrawerCallback getCurrentCallback() {
        final Object f = mPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
        if (f instanceof HeaderDrawerLayout.DrawerCallback)
            return (HeaderDrawerLayout.DrawerCallback) f;
        return null;
    }

    @Override
    public void topChanged(final int offset) {
        if (mActionBarBackground == null || mMainContent == null) return;
        final int paddingTop = mHeaderDrawerLayout.getPaddingTop();
        final int spaceHeight = mHeaderSpaceLayout.getMeasuredHeight();
        if (spaceHeight == 0) {
            mActionBarBackground.setFactor(0);
            mMainContent.setFactor(0);
            return;
        }
        final float factor = (paddingTop - offset) / (float) spaceHeight;
        mActionBarBackground.setFactor(factor);
        mMainContent.setFactor(factor);
        mBannerImageView.setTranslationY(MathUtils.clamp(offset, 0, -mBannerImageView.getHeight()) * 0.3f);
    }

    @Override
    public void onFitSystemWindows(final Rect insets) {
        mHeaderDrawerLayout.setPadding(insets.left, insets.top, insets.right, insets.bottom);
        mHeaderSpaceLayout.setMinusTop(insets.top);
    }

    @Override
    protected boolean isTintBarEnabled() {
        return false;
    }
}
