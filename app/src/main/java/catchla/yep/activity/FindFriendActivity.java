package catchla.yep.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import catchla.yep.R;
import catchla.yep.util.ThemeUtils;
import catchla.yep.view.TintedStatusFrameLayout;

/**
 * Created by mariotaku on 15/6/30.
 */
public class FindFriendActivity extends SwipeBackContentActivity {

    private TintedStatusFrameLayout mMainContent;

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
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mMainContent = (TintedStatusFrameLayout) findViewById(R.id.main_content);
    }
}
