/*
 * Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
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

package catchla.yep.util

import android.support.v4.view.MenuItemCompat
import android.view.Menu

fun Menu?.setItemAvailability(id: Int, available: Boolean) {
    val item = this?.findItem(id) ?: return
    item.isVisible = available
    item.isEnabled = available
}

fun Menu?.setMenuGroupAvailability(groupId: Int, available: Boolean) {
    if (this == null) return
    setGroupEnabled(groupId, available)
    setGroupVisible(groupId, available)
}

/**
 * Created by mariotaku on 15/4/12.
 */
object MenuUtils {


    fun setMenuItemChecked(menu: Menu?, id: Int, checked: Boolean) {
        if (menu == null) return
        val item = menu.findItem(id) ?: return
        item.isChecked = checked
    }

    fun setMenuItemIcon(menu: Menu?, id: Int, icon: Int) {
        if (menu == null) return
        val item = menu.findItem(id) ?: return
        item.setIcon(icon)
    }

    fun setMenuItemShowAsActionFlags(menu: Menu?, id: Int, flags: Int) {
        if (menu == null) return
        val item = menu.findItem(id) ?: return
        item.setShowAsActionFlags(flags)
        MenuItemCompat.setShowAsAction(item, flags)
    }

    fun setMenuItemTitle(menu: Menu?, id: Int, icon: Int) {
        if (menu == null) return
        val item = menu.findItem(id) ?: return
        item.setTitle(icon)
    }
}
