package catchla.yep.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import catchla.yep.Constants.*
import catchla.yep.R
import catchla.yep.extension.account
import catchla.yep.fragment.TopicsListFragment
import catchla.yep.model.Skill

/**
 * Created by mariotaku on 15/10/27.
 */
class SkillUpdatesActivity : SwipeBackContentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skill_update)
        title = skill.nameString
        val ft = supportFragmentManager.beginTransaction()
        val args = Bundle()
        args.putParcelable(EXTRA_ACCOUNT, account)
        args.putParcelable(EXTRA_SKILL, skill)
        ft.replace(R.id.mainContent, Fragment.instantiate(this, TopicsListFragment::class.java.name, args))
        ft.commit()
    }

    private val skill: Skill
        get() = intent.getParcelableExtra<Skill>(EXTRA_SKILL)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_topic -> {
                val intent = Intent(this, NewTopicActivity::class.java)
                intent.putExtra(EXTRA_ACCOUNT, account)
                intent.putExtra(EXTRA_SKILL, skill)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_skill_updates, menu)
        return true
    }
}
