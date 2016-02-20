package catchla.yep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.fragment.SearchUsersFragment;

/**
 * Created by mariotaku on 15/9/2.
 */
public class SearchActivity extends SwipeBackContentActivity implements Constants {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_content);

        final FragmentManager fm = getSupportFragmentManager();
        final SearchUsersFragment f = new SearchUsersFragment();
        final Bundle args = new Bundle();
        final Intent intent = getIntent();
        args.putParcelable(EXTRA_ACCOUNT, getAccount());
        args.putString(EXTRA_QUERY, intent.getStringExtra(EXTRA_QUERY));
        f.setArguments(args);
        fm.beginTransaction().replace(R.id.main_content, f).commit();
    }
}
