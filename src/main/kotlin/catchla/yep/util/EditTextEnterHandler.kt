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

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import java.util.*

/**
 * Created by mariotaku on 15/4/22.
 */
class EditTextEnterHandler(
        var enabled: Boolean,
        var listener: (() -> Unit)?
) : View.OnKeyListener, OnEditorActionListener, TextWatcher {
    private var textWatchers: ArrayList<TextWatcher>? = null

    fun addTextChangedListener(watcher: TextWatcher) {
        if (textWatchers == null) {
            textWatchers = ArrayList<TextWatcher>()
        }
        textWatchers!!.add(watcher)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        if (textWatchers == null) return
        for (textWatcher in textWatchers!!) {
            textWatcher.beforeTextChanged(s, start, count, after)
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (textWatchers == null) return
        for (textWatcher in textWatchers!!) {
            textWatcher.onTextChanged(s, start, before, count)
        }
    }

    override fun afterTextChanged(s: Editable) {
        val length = s.length
        if (enabled && length > 0 && s[length - 1] == '\n') {
            s.delete(length - 1, length)
            listener?.invoke()
        } else if (textWatchers != null) {
            for (textWatcher in textWatchers!!) {
                textWatcher.afterTextChanged(s)
            }
        }
    }

    override fun onEditorAction(view: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (!enabled) return false
        if (event != null && actionId == EditorInfo.IME_NULL && event.action == KeyEvent.ACTION_DOWN) {
            listener?.invoke()
            return true
        }
        return false
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER && enabled && event.action == KeyEvent.ACTION_DOWN) {
            listener?.invoke()
            return true
        }
        return false
    }

    companion object {

        fun attach(editText: EditText, enabled: Boolean, listener: (() -> Unit)? = null): EditTextEnterHandler {
            val enterHandler = EditTextEnterHandler(enabled, listener)
            editText.setOnKeyListener(enterHandler)
            editText.setOnEditorActionListener(enterHandler)
            editText.addTextChangedListener(enterHandler)
            return enterHandler
        }
    }

}
