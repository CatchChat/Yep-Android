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

package catchla.yep.fragment

import android.content.Context
import android.support.v7.widget.FixedLinearLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import catchla.yep.adapter.LoadMoreSupportAdapter
import catchla.yep.adapter.decorator.DividerItemDecoration
import catchla.yep.adapter.iface.ILoadMoreSupportAdapter.IndicatorPosition


/**
 * Comment, blah, blah, blah.
 * Created by mariotaku on 15/4/16.
 */
abstract class AbsContentListRecyclerViewFragment<A : LoadMoreSupportAdapter<RecyclerView.ViewHolder>> : AbsContentRecyclerViewFragment<A, LinearLayoutManager>() {

    private var itemDecoration: DividerItemDecoration? = null

    override fun setupRecyclerView(context: Context, recyclerView: RecyclerView, layoutManager: LinearLayoutManager) {
        itemDecoration = DividerItemDecoration(context, layoutManager.orientation)
        recyclerView.addItemDecoration(itemDecoration)
    }

    override fun setLoadMoreIndicatorPosition(@IndicatorPosition position: Int) {
        if (itemDecoration != null) {
            itemDecoration!!.setDecorationEndOffset(if (position == IndicatorPosition.END) 1 else 0)
        }
        super.setLoadMoreIndicatorPosition(position)
    }


    override fun onScrollToPositionWithOffset(layoutManager: LinearLayoutManager, position: Int, offset: Int) {
        layoutManager.scrollToPositionWithOffset(0, 0)
    }

    override fun onCreateLayoutManager(context: Context): LinearLayoutManager {
        return FixedLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun isReachingEnd(): Boolean {
        return layoutManager.findLastCompletelyVisibleItemPosition() >= layoutManager.itemCount - 1
    }

    override fun isReachingStart(): Boolean {
        return layoutManager.findFirstCompletelyVisibleItemPosition() <= 0
    }

}
