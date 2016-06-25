/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity

import android.os.Bundle
import android.preference.PreferenceActivity
import android.support.v4.app.Fragment
import catchla.yep.Constants
import catchla.yep.R

class SettingsActivity : ContentActivity(), Constants {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_content)
        val intent = intent
        val fname = intent.getStringExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT)
        val args = intent.getBundleExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_ARGUMENTS)
        val fragment = Fragment.instantiate(this, fname, args)
        supportFragmentManager.beginTransaction().replace(R.id.mainContent, fragment).commit()
    }
}
