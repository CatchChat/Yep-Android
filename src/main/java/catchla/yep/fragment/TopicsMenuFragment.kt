package catchla.yep.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import catchla.yep.Constants

import catchla.yep.R
import catchla.yep.model.Topic

/**
 * Created by mariotaku on 15/12/6.
 */
class TopicsMenuFragment : FloatingActionMenuFragment(), AdapterView.OnItemSelectedListener {
    lateinit var mOrderSpinner: Spinner

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = ArrayAdapter<Entry>(context, android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter.add(Entry(Topic.SortOrder.DEFAULT, getString(R.string.sort_order_default)))
        adapter.add(Entry(Topic.SortOrder.DISTANCE, getString(R.string.distance)))
        adapter.add(Entry(Topic.SortOrder.TIME, getString(R.string.time)))
        mOrderSpinner.adapter = adapter
        mOrderSpinner.onItemSelectedListener = this
        val sortOrder = preferences.getString(Constants.KEY_TOPICS_SORT_ORDER, null)
        var i = 0
        val j = adapter.count
        while (i < j) {
            if (adapter.getItem(i).sortBy == sortOrder) {
                mOrderSpinner.setSelection(i)
                break
            }
            i++
        }
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onBaseViewCreated(view, savedInstanceState)
        mOrderSpinner = view.findViewById(R.id.order_spinner) as Spinner
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_topics_floating_menu, container, false)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val fragment = belongsTo as TopicsListFragment?
        fragment?.reloadWithSortOrder((mOrderSpinner.getItemAtPosition(position) as Entry).sortBy)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    internal class Entry(internal val sortBy: String, internal val title: String) {

        override fun toString(): String {
            return title
        }
    }
}
