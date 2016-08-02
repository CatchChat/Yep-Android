/*
 * Twidere - Twitter client for Android
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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.support.v4.graphics.ColorUtils
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import catchla.yep.R
import catchla.yep.util.Utils
import catchla.yep.util.support.WindowSupport
import catchla.yep.view.iface.TintedStatusLayout

/**
 * Created by mariotaku on 14/11/26.
 */
class TintedStatusFrameLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ExtendedFrameLayout(context, attrs, defStyleAttr), TintedStatusLayout {

    private val mColorPaint: Paint
    private var mSetPadding: Boolean = false

    private var mStatusBarHeight: Int = 0
    private val mSystemWindowsInsets: Rect
    private var mWindowInsetsListener: WindowInsetsListener? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.TintedStatusLayout)
        setSetPaddingEnabled(a.getBoolean(R.styleable.TintedStatusLayout_setPadding, false))
        a.recycle()
        mColorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSystemWindowsInsets = Rect()
        setWillNotDraw(false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
                val top = insets.systemWindowInsetTop
                val left = insets.systemWindowInsetLeft
                val right = insets.systemWindowInsetRight
                val bottom = insets.systemWindowInsetBottom
                if (mSetPadding) {
                    setPadding(left, top, right, bottom)
                }
                setStatusBarHeight(top)
                if (mWindowInsetsListener != null) {
                    mWindowInsetsListener!!.onApplyWindowInsets(left, top, right, bottom)
                }
                insets.consumeSystemWindowInsets()
            }
        }
    }


    override fun setStatusBarColorDarken(color: Int) {
        mColorPaint.color = 0xFF000000.toInt() or Utils.getColorDark(color)
        mColorPaint.alpha = Color.alpha(color)
        invalidate()
    }

    override fun setSetPaddingEnabled(enabled: Boolean) {
        mSetPadding = enabled
    }

    fun setStatusBarHeight(height: Int) {
        mStatusBarHeight = height
        invalidate()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), mStatusBarHeight.toFloat(), mColorPaint)
    }

    override fun fitSystemWindows(insets: Rect): Boolean {
        mSystemWindowsInsets.set(insets)
        return true
    }

    fun setWindowInsetsListener(listener: WindowInsetsListener) {
        mWindowInsetsListener = listener
    }

    interface WindowInsetsListener {
        fun onApplyWindowInsets(left: Int, top: Int, right: Int, bottom: Int)
    }
}
