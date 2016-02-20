package catchla.yep.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.fragment.TopicsListFragment;
import catchla.yep.model.User;

/**
 * Created by mariotaku on 15/9/2.
 */
public class UserTopicsActivity extends SwipeBackContentActivity implements Constants {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_content);

        final FragmentManager fm = getSupportFragmentManager();
        final TopicsListFragment f = new TopicsListFragment();
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_ACCOUNT, getAccount());
        args.putString(EXTRA_USER_ID, getUser().getId());
        f.setArguments(args);
        fm.beginTransaction().replace(R.id.main_content, f).commit();
    }

    private User getUser() {
        return getIntent().getParcelableExtra(EXTRA_USER);
    }
}
