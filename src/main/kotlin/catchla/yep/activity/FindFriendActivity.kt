package catchla.yep.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.view.Menu
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.util.ThemeUtils
import catchla.yep.view.TintedStatusFrameLayout
import com.commonsware.cwac.merge.MergeAdapter

/**
 * Created by mariotaku on 15/6/30.
 */
class FindFriendActivity : SwipeBackContentActivity(), Constants {

    private lateinit var mMainContent: TintedStatusFrameLayout
    private lateinit var mListView: ListView
    private var mAdapter: MergeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_friend)
        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        val primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0)
        actionBar.setBackgroundDrawable(ThemeUtils.getActionBarBackground(primaryColor, true))


        mAdapter = MergeAdapter()
        val actionsAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        actionsAdapter.add(getString(R.string.contact_friends))
        mAdapter!!.addAdapter(actionsAdapter)
        mListView.adapter = mAdapter
        mListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if (position == 1) {
                startActivity(Intent(this@FindFriendActivity, ContactFriendsActivity::class.java))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_find_friend, menu)
        val searchView = MenuItemCompat.getActionView(menu.findItem(R.id.search)) as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val intent = Intent(this@FindFriendActivity, SearchActivity::class.java)
                intent.putExtra(Constants.EXTRA_ACCOUNT, account)
                intent.putExtra(Constants.EXTRA_QUERY, query)
                startActivity(intent)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return true
    }

    override fun onContentChanged() {
        super.onContentChanged()
        mMainContent = findViewById(R.id.main_content) as TintedStatusFrameLayout
        mListView = findViewById(R.id.list_view) as ListView
    }

}
