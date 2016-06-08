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

package catchla.yep.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView.ViewHolder

import catchla.yep.adapter.iface.ILoadMoreSupportAdapter


/**
 * Created by mariotaku on 15/4/16.
 */
abstract class LoadMoreSupportAdapter<VH : ViewHolder>(context: Context) : BaseRecyclerViewAdapter<VH>(context), ILoadMoreSupportAdapter {

    @ILoadMoreSupportAdapter.IndicatorPosition
    private var loadMoreSupportedPosition: Int = 0
    @ILoadMoreSupportAdapter.IndicatorPosition
    private var loadMoreIndicatorPosition: Int = 0

    @ILoadMoreSupportAdapter.IndicatorPosition
    override fun getLoadMoreIndicatorPosition(): Int {
        return loadMoreIndicatorPosition
    }

    override fun setLoadMoreIndicatorPosition(@ILoadMoreSupportAdapter.IndicatorPosition position: Int) {
        if (loadMoreIndicatorPosition == position) return
        loadMoreIndicatorPosition = ILoadMoreSupportAdapter.IndicatorPositionUtils.apply(position, loadMoreSupportedPosition)
        notifyDataSetChanged()
    }

    @ILoadMoreSupportAdapter.IndicatorPosition
    override fun getLoadMoreSupportedPosition(): Int {
        return loadMoreSupportedPosition
    }

    override fun setLoadMoreSupportedPosition(@ILoadMoreSupportAdapter.IndicatorPosition supportedPosition: Int) {
        loadMoreSupportedPosition = supportedPosition
        loadMoreIndicatorPosition = ILoadMoreSupportAdapter.IndicatorPositionUtils.apply(loadMoreIndicatorPosition, supportedPosition)
        notifyDataSetChanged()
    }

}
