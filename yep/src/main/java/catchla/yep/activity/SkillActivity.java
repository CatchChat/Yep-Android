package catchla.yep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.adapter.TabsAdapter;
import catchla.yep.fragment.DiscoverFragment;
import catchla.yep.graphic.EmptyDrawable;
import catchla.yep.model.Skill;
import catchla.yep.util.ThemeUtils;
import catchla.yep.view.TabPagerIndicator;

/**
 * Created by mariotaku on 15/6/2.
 */
public class SkillActivity extends SwipeBackContentActivity implements Constants {

    private ViewPager mViewPager;
    private TabPagerIndicator mPagerTab;
    private View mPagerOverlay;

    private TabsAdapter mPagerAdapter;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerTab = (TabPagerIndicator) findViewById(R.id.view_pager_tabs);
        mPagerOverlay = findViewById(R.id.pager_window_overlay);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill);
        mPagerAdapter = new TabsAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mPagerTab.setViewPager(mViewPager);
        mPagerTab.updateAppearance();

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


        ThemeUtils.initPagerIndicatorAsActionBarTab(this, mPagerTab, mPagerOverlay);
        ThemeUtils.setCompatToolbarOverlay(this, new EmptyDrawable());
        ThemeUtils.setCompatContentViewOverlay(this, new EmptyDrawable());
    }

    private void displaySkill(final Skill skill) {
        setTitle(skill.getNameString());
    }

}
