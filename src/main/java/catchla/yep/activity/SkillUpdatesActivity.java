package catchla.yep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.model.Skill;

/**
 * Created by mariotaku on 15/10/27.
 */
public class SkillUpdatesActivity extends SwipeBackContentActivity implements Constants, View.OnClickListener {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_update);
        final Skill skill = getSkill();
        setTitle(skill.getName());
        findViewById(R.id.related_users).setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.related_users: {
                final Skill skill = getSkill();
                final Intent intent = new Intent(this, SkillUsersActivity.class);
                intent.putExtra(EXTRA_SKILL, skill);
                intent.putExtra(EXTRA_ACCOUNT, getAccount());
                startActivity(intent);
                break;
            }
        }
    }

    private Skill getSkill() {
        return getIntent().getParcelableExtra(EXTRA_SKILL);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_topic: {
                final Intent intent = new Intent(this, NewTopicActivity.class);
                intent.putExtra(EXTRA_ACCOUNT, getAccount());
                intent.putExtra(EXTRA_SKILL, getSkill());
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_skill_updates, menu);
        return true;
    }
}
