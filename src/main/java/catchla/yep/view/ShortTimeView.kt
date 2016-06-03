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

package catchla.yep.view

import android.content.Context
import android.os.SystemClock
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import catchla.yep.util.Utils

class ShortTimeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = android.R.attr.textViewStyle) : AppCompatTextView(context, attrs, defStyle) {


    private val ticker: Runnable
    private var mTime: Long = 0

    init {
        ticker = TickerRunnable(this)
    }

    fun setTime(time: Long) {
        mTime = time
        invalidateTime()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post(ticker)
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(ticker)
        super.onDetachedFromWindow()
    }

    private fun invalidateTime() {
        text = Utils.formatSameDayTime(context, mTime)
    }

    private class TickerRunnable constructor(val textView: ShortTimeView) : Runnable {

        private val TICKER_DURATION = 5000L

        override fun run() {
            val handler = textView.handler ?: return
            textView.invalidateTime()
            val now = SystemClock.uptimeMillis()
            val next = now + TICKER_DURATION - now % TICKER_DURATION
            handler.postAtTime(this, next)
        }
    }

}
