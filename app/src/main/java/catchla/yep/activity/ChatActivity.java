/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.FixedLinearLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import catchla.yep.R;
import catchla.yep.util.ThemeUtils;
import catchla.yep.view.TintedStatusFrameLayout;

/**
 * Created by mariotaku on 15/4/30.
 */
public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TintedStatusFrameLayout mMainContent;


    private FixedLinearLayoutManager mLayoutManager;

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mMainContent = (TintedStatusFrameLayout) findViewById(R.id.main_content);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        final int primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0);
        actionBar.setBackgroundDrawable(ThemeUtils.getActionBarBackground(primaryColor, true));

        mMainContent.setDrawColor(true);
        mMainContent.setDrawShadow(false);
        mMainContent.setColor(primaryColor);
        mLayoutManager = new FixedLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

}
