package catchla.yep.activity;

import android.os.Bundle;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.fragment.ChatsListFragment;
import catchla.yep.model.Message;

/**
 * Created by mariotaku on 15/11/4.
 */
public class CirclesListActivity extends SwipeBackContentActivity implements Constants {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_content);
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_ACCOUNT, getAccount());
        args.putString(ChatsListFragment.EXTRA_RECIPIENT_TYPE, Message.RecipientType.CIRCLE);
        final ChatsListFragment f = new ChatsListFragment();
        f.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, f).commit();
    }
}
