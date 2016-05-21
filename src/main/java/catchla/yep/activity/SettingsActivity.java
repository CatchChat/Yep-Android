/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;

import catchla.yep.Constants;
import catchla.yep.R;

public class SettingsActivity extends ContentActivity implements Constants {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_content);
        final Intent intent = getIntent();
        final String fname = intent.getStringExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT);
        Bundle args = intent.getBundleExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_ARGUMENTS);
        Fragment fragment = Fragment.instantiate(this, fname, args);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).commit();
    }
}
