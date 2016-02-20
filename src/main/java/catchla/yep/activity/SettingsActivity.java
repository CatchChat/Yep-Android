/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.util.ThemeUtils;
import catchla.yep.view.TintedStatusFrameLayout;

public class SettingsActivity extends AppCompatPreferenceActivity implements Constants {
    private TintedStatusFrameLayout mMainContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // TODO: If Settings has multiple levels, Up should navigate up
            // that hierarchy.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mMainContent = (TintedStatusFrameLayout) findViewById(R.id.main_content);
        setupTintStatusBar();
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        try {
            return Fragment.class.isAssignableFrom(Class.forName(fragmentName));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private void setupTintStatusBar() {
        final ActionBar actionBar = getSupportActionBar();
        if (mMainContent == null || actionBar == null) return;

        final int primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0);
        actionBar.setBackgroundDrawable(ThemeUtils.getActionBarBackground(primaryColor, true));
        mMainContent.setColor(primaryColor);

        mMainContent.setDrawShadow(false);
        mMainContent.setDrawColor(true);
        mMainContent.setFactor(1);
    }

    @Override
    public void startActivityForResult(final Intent intent, final int requestCode) {
        addAccountInfo(intent);
        super.startActivityForResult(intent, requestCode);
    }

    private void addAccountInfo(final Intent intent) {
        if (intent.hasExtra(EXTRA_ACCOUNT)) return;
        intent.putExtra(EXTRA_ACCOUNT, getIntent().getParcelableExtra(EXTRA_ACCOUNT));
    }

    @Override
    public void startActivityForResult(final Intent intent, final int requestCode, final Bundle options) {
        addAccountInfo(intent);
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        final FrameLayout mainContent = initMainContent();
        getLayoutInflater().inflate(layoutResID, (ViewGroup) mainContent.findViewById(R.id.main_content), true);
        super.setContentView(mainContent);
    }

    @Override
    public void setContentView(View view) {
        final FrameLayout mainContent = initMainContent();
        final ViewGroup settingsContent = (ViewGroup) mainContent.findViewById(R.id.main_content);
        settingsContent.removeAllViews();
        settingsContent.addView(view);
        super.setContentView(mainContent);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        final FrameLayout mainContent = initMainContent();
        final ViewGroup settingsContent = (ViewGroup) mainContent.findViewById(R.id.main_content);
        settingsContent.removeAllViews();
        settingsContent.addView(view);
        super.setContentView(mainContent);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        FrameLayout mainContent = (FrameLayout) findViewById(R.id.main_content);
        if (mainContent == null) {
            @SuppressLint("InflateParams")
            final View mainLayout = getLayoutInflater().inflate(R.layout.activity_settings, null);
            mainContent = (FrameLayout) mainLayout.findViewById(R.id.main_content);
        }
        final ViewGroup settingsContent = (ViewGroup) mainContent.findViewById(R.id.main_content);
        settingsContent.addView(view, params);
        onContentChanged();
    }

    private FrameLayout initMainContent() {
        final FrameLayout mainContent = (FrameLayout) findViewById(R.id.main_content);
        if (mainContent != null) {
            return mainContent;
        }
        return ((FrameLayout) getLayoutInflater().inflate(R.layout.activity_settings, null));
    }

}
