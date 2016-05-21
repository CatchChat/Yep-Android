/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import catchla.yep.Constants;

public class SettingsActivity extends ContentActivity implements Constants {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        intent.getStringExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT);
    }
}
