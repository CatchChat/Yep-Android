package catchla.yep.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import java.util.*

/**
 * Created by mariotaku on 15/8/31.
 */
open class LayoutAdapter(val context: Context) : BaseAdapter() {

    private val items = ArrayList<LayoutItem>()

    private val inflater: LayoutInflater
    private var itemsFinal: Boolean = false

    init {
        inflater = LayoutInflater.from(context)
    }

    fun clear() {
        checkFinal()
        items.clear()
    }

    fun add(layoutRes: Int, tag: String, enabled: Boolean) {
        checkFinal()
        items.add(LayoutItem(layoutRes, tag, enabled))
    }

    fun makeFinal() {
        checkFinal()
        itemsFinal = true
        notifyDataSetChanged()
    }

    private fun checkFinal() {
        if (itemsFinal) throw IllegalStateException("Layouts are final")
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): LayoutItem {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return System.identityHashCode(getItem(position)).toLong()
    }

    override fun isEnabled(position: Int): Boolean {
        return getItem(position).enabled
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewTypeCount(): Int {
        return count
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val item = getItem(position)
        if (convertView != null) {
            view = convertView
        } else {
            view = inflater.inflate(item.layoutRes, parent, false)
        }
        bindView(view, position, item.tag)
        return view
    }

    protected open fun bindView(view: View, position: Int, tag: String) {
    }

    class LayoutItem internal constructor(internal var layoutRes: Int, internal var tag: String, internal var enabled: Boolean)
}
