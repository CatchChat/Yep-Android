/*
 * 				Twidere - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package catchla.yep.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceScreen
import catchla.yep.Constants
import catchla.yep.util.Utils

class SettingsDetailsFragment : PreferenceFragmentCompat(), Constants, SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val preferenceManager = preferenceManager
        val defaultScreen = preferenceScreen
        val preferenceScreen: PreferenceScreen
        if (defaultScreen != null) {
            defaultScreen.removeAll()
            preferenceScreen = defaultScreen
        } else {
            preferenceScreen = preferenceManager.createPreferenceScreen(activity)
        }
        setPreferenceScreen(preferenceScreen)
        val args = arguments
        val rawResId = args.get(Constants.EXTRA_RESID)
        val resId: Int
        if (rawResId is Int) {
            resId = rawResId
        } else if (rawResId is String) {
            resId = Utils.getResId(activity, rawResId)
        } else {
            resId = 0
        }
        if (resId != 0) {
            addPreferencesFromResource(resId)
        }
    }

    override fun onStart() {
        super.onStart()
        val preferences = preferenceManager.sharedPreferences
        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        val preferences = preferenceManager.sharedPreferences
        preferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val preference = findPreference(key) ?: return
        val extras = preference.extras
        //        if (extras != null && extras.containsKey(EXTRA_NOTIFY_CHANGE)) {
        //            SettingsActivity.setShouldNotifyChange(getActivity());
        //        }
    }
}
