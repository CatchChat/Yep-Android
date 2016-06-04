package catchla.yep.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View

import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.model.Skill

/**
 * Created by mariotaku on 15/10/27.
 */
class SkillUpdatesActivity : SwipeBackContentActivity(), Constants, View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skill_update)
        val skill = skill
        title = skill.name
        findViewById(R.id.related_users)!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.related_users -> {
                val skill = skill
                val intent = Intent(this, SkillUsersActivity::class.java)
                intent.putExtra(Constants.EXTRA_SKILL, skill)
                intent.putExtra(Constants.EXTRA_ACCOUNT, account)
                startActivity(intent)
            }
        }
    }

    private val skill: Skill
        get() = intent.getParcelableExtra<Skill>(Constants.EXTRA_SKILL)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_topic -> {
                val intent = Intent(this, NewTopicActivity::class.java)
                intent.putExtra(Constants.EXTRA_ACCOUNT, account)
                intent.putExtra(Constants.EXTRA_SKILL, skill)
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
