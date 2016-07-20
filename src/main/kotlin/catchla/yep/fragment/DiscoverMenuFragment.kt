package catchla.yep.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import catchla.yep.Constants.KEY_DISCOVER_SORT_ORDER
import catchla.yep.R
import catchla.yep.model.DiscoverSortOrder
import kotlinx.android.synthetic.main.fragment_spinner_floating_menu.*

/**
 * Created by mariotaku on 15/12/6.
 */
class DiscoverMenuFragment : FloatingActionMenuFragment(), AdapterView.OnItemSelectedListener {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = ArrayAdapter<Entry>(context, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter.add(Entry(DiscoverSortOrder.SCORE, getString(R.string.sort_order_default)))
        adapter.add(Entry(DiscoverSortOrder.DISTANCE, getString(R.string.distance)))
        adapter.add(Entry(DiscoverSortOrder.LAST_SIGN_IN_AT, getString(R.string.time)))
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
        val sortOrder = preferences.getString(KEY_DISCOVER_SORT_ORDER, null)
        for (i in 0..adapter.count - 1) {
            if (adapter.getItem(i).sortBy == sortOrder) {
                spinner.setSelection(i)
                break
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_spinner_floating_menu, container, false)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val fragment = belongsTo as DiscoverFragment?
        fragment?.reloadWithSortOrder((spinner.getItemAtPosition(position) as Entry).sortBy)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    internal class Entry(internal val sortBy: String, internal val title: String) {

        override fun toString(): String {
            return title
        }
    }
}
