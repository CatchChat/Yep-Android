package catchla.yep.activity;

import android.accounts.Account;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.ThemeUtils;
import catchla.yep.util.dagger.ApplicationModule;
import catchla.yep.util.dagger.DaggerGeneralComponent;
import catchla.yep.view.TintedStatusFrameLayout;

public class ContentActivity extends AppCompatActivity {

    private TintedStatusFrameLayout mMainContent;
    @Inject
    protected Bus mBus;
    @Inject
    protected ImageLoaderWrapper mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerGeneralComponent.builder().applicationModule(ApplicationModule.get(this)).build().inject(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupTintStatusBar();
    }

    public Account getAccount() {
        return getIntent().getParcelableExtra(Constants.EXTRA_ACCOUNT);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mMainContent = (TintedStatusFrameLayout) findViewById(R.id.main_content);
    }

    protected TintedStatusFrameLayout getMainContent() {
        return mMainContent;
    }

    protected boolean isTintBarEnabled() {
        return true;
    }

    private void setupTintStatusBar() {
        final ActionBar actionBar = getSupportActionBar();
        if (mMainContent == null || actionBar == null || !isTintBarEnabled()) return;

        final int primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0);
        actionBar.setBackgroundDrawable(ThemeUtils.getActionBarBackground(primaryColor, true));
        mMainContent.setColor(primaryColor);

        mMainContent.setDrawShadow(false);
        mMainContent.setDrawColor(true);
        mMainContent.setFactor(1);
    }

}
