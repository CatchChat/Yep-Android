package catchla.yep.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import catchla.yep.R;
import catchla.yep.fragment.BlockedUsersFragment;

/**
 * Created by mariotaku on 15/10/10.
 */
public class BlockedUsersActivity extends SwipeBackContentActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_content);
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_content, new BlockedUsersFragment());
        ft.commit();
    }
}
