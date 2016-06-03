package catchla.yep.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import catchla.yep.model.TabSpec
import catchla.yep.view.iface.PagerIndicator
import java.util.*

/**
 * Created by mariotaku on 15/5/21.
 */
class TabsAdapter(private val context: Context, fm: FragmentManager) : SupportFixedFragmentStatePagerAdapter(fm), PagerIndicator.TabProvider, PagerIndicator.TabListener {
    var primaryItem: Fragment? = null
        private set
    var tabListener: PagerIndicator.TabListener? = null
        set

    private val tabs = ArrayList<TabSpec>()

    override fun getItem(position: Int): Fragment {
        val spec = tabs[position]
        return Fragment.instantiate(context, spec.cls.name, spec.args)
    }

    fun addTab(cls: Class<out Fragment>, title: CharSequence, icon: Int, args: Bundle?) {
        tabs.add(TabSpec(cls, title, icon, args))
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return tabs.size
    }

    override fun getPageIcon(position: Int): Drawable? {
        val spec = tabs[position]
        if (spec.icon == 0) return null
        return ContextCompat.getDrawable(context, spec.icon)
    }

    override fun getPageTitle(position: Int): CharSequence {
        val spec = tabs[position]
        return spec.title
    }

    override fun setPrimaryItem(container: ViewGroup?, position: Int, obj: Any) {
        super.setPrimaryItem(container, position, obj)
        primaryItem = obj as Fragment
    }

    override fun onPageReselected(position: Int) {
        if (tabListener == null) return
        tabListener!!.onPageReselected(position)
    }

    override fun onTabLongClick(position: Int): Boolean {
        if (tabListener == null) return false
        return tabListener!!.onTabLongClick(position)
    }

    override fun onPageSelected(position: Int) {
        if (tabListener == null) return
        tabListener!!.onPageSelected(position)
    }

}
