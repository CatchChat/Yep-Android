package catchla.yep.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.adapter.TabsAdapter;
import catchla.yep.fragment.DiscoverFragment;
import catchla.yep.graphic.EmptyDrawable;
import catchla.yep.model.Skill;
import catchla.yep.util.ThemeUtils;
import catchla.yep.util.Utils;
import catchla.yep.view.TabPagerIndicator;

/**
 * Created by mariotaku on 15/6/2.
 */
public class SkillUsersActivity extends SwipeBackContentActivity implements Constants {

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
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_skill_users, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        final MenuItem itemSkill = menu.findItem(R.id.add_skill);
        final boolean hasSkill = Utils.hasSkill(Utils.getAccountUser(this, getAccount()), getSkill());
        itemSkill.setVisible(!hasSkill);
        itemSkill.setEnabled(!hasSkill);
        return true;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill);
        mPagerAdapter = new TabsAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mPagerTab.setViewPager(mViewPager);
        mPagerTab.updateAppearance();

        final Skill skill = getSkill();
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

    private Skill getSkill() {
        return getIntent().getParcelableExtra(EXTRA_SKILL);
    }

    private void displaySkill(final Skill skill) {
        setTitle(skill.getNameString());
    }

}
