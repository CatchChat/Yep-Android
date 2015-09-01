package catchla.yep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.commonsware.cwac.merge.MergeAdapter;

import catchla.yep.R;
import catchla.yep.util.ThemeUtils;
import catchla.yep.view.TintedStatusFrameLayout;

/**
 * Created by mariotaku on 15/6/30.
 */
public class FindFriendActivity extends SwipeBackContentActivity {

    private TintedStatusFrameLayout mMainContent;
    private ListView mListView;
    private MergeAdapter mAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        final int primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0);
        actionBar.setBackgroundDrawable(ThemeUtils.getActionBarBackground(primaryColor, true));

        mMainContent.setDrawColor(true);
        mMainContent.setDrawShadow(false);
        mMainContent.setColor(primaryColor);

        mAdapter = new MergeAdapter();
        mAdapter.addView(getLayoutInflater().inflate(R.layout.list_header_add_friend_search, mListView, false), false);
        final ArrayAdapter<String> actionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        actionsAdapter.add(getString(R.string.contact_friends));
        mAdapter.addAdapter(actionsAdapter);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                if (position == 1) {
                    startActivity(new Intent(FindFriendActivity.this, ContactFriendsActivity.class));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_find_friend, menu);
        return true;
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mMainContent = (TintedStatusFrameLayout) findViewById(R.id.main_content);
        mListView = (ListView) findViewById(R.id.list_view);
    }

}
