package catchla.yep.activity

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.View
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.adapter.TabsAdapter
import catchla.yep.fragment.DiscoverFragment
import catchla.yep.graphic.EmptyDrawable
import catchla.yep.model.Skill
import catchla.yep.util.ThemeUtils
import catchla.yep.util.Utils
import org.mariotaku.ktextension.setItemAvailability
import catchla.yep.view.TabPagerIndicator

/**
 * Created by mariotaku on 15/6/2.
 */
class SkillUsersActivity : SwipeBackContentActivity(), Constants {

    private var mViewPager: ViewPager? = null
    private var mPagerTab: TabPagerIndicator? = null
    private var mPagerOverlay: View? = null

    private var mPagerAdapter: TabsAdapter? = null

    override fun onContentChanged() {
        super.onContentChanged()
        mViewPager = findViewById(R.id.view_pager) as ViewPager?
        mPagerTab = findViewById(R.id.view_pager_tabs) as TabPagerIndicator?
        mPagerOverlay = findViewById(R.id.pager_window_overlay)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_skill_users, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val hasSkill = Utils.hasSkill(Utils.getAccountUser(this, account), skill)
        menu.setItemAvailability(R.id.add_skill, !hasSkill)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skill)
        mPagerAdapter = TabsAdapter(this, supportFragmentManager)
        mViewPager!!.adapter = mPagerAdapter
        mPagerTab!!.setViewPager(mViewPager)
        mPagerTab!!.updateAppearance()

        val skill = skill
        displaySkill(skill)
        val masterArgs = Bundle()
        masterArgs.putParcelable(Constants.EXTRA_ACCOUNT, account)
        masterArgs.putStringArray(Constants.EXTRA_MASTER, arrayOf(skill.id))
        val learningArgs = Bundle()
        learningArgs.putParcelable(Constants.EXTRA_ACCOUNT, account)
        learningArgs.putStringArray(Constants.EXTRA_LEARNING, arrayOf(skill.id))
        mPagerAdapter!!.addTab(DiscoverFragment::class.java, getString(R.string.master), 0, masterArgs)
        mPagerAdapter!!.addTab(DiscoverFragment::class.java, getString(R.string.learning), 0, learningArgs)


        ThemeUtils.initPagerIndicatorAsActionBarTab(this, mPagerTab, mPagerOverlay)
        ThemeUtils.setCompatToolbarOverlay(this, EmptyDrawable())
        ThemeUtils.setCompatContentViewOverlay(this, EmptyDrawable())
    }

    private val skill: Skill
        get() = intent.getParcelableExtra<Skill>(Constants.EXTRA_SKILL)

    private fun displaySkill(skill: Skill) {
        title = skill.nameString
    }

}
