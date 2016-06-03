package catchla.yep.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.adapter.TabsAdapter
import catchla.yep.loader.SkillCategoriesLoader
import catchla.yep.model.Skill
import catchla.yep.model.SkillCategory
import catchla.yep.model.TaskResponse
import catchla.yep.util.Utils
import java.util.*

/**
 * Created by mariotaku on 15/6/10.
 */
class SkillSelectorActivity : ContentActivity(), Constants {

    private lateinit var viewPager: ViewPager
    private lateinit var adapter: TabsAdapter
    var selectedCategory: SkillCategory? = null

    private lateinit var mSelectedSkills: ArrayList<Skill>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_skill)

        mSelectedSkills = intent.getParcelableArrayListExtra<Skill>(Constants.EXTRA_SKILLS)

        adapter = TabsAdapter(this, supportFragmentManager)
        viewPager.isEnabled = false
        viewPager.adapter = adapter

        adapter.addTab(CategoriesFragment::class.java, null, 0, null)
        adapter.addTab(SkillsFragment::class.java, null, 0, null)
    }

    override fun onContentChanged() {
        super.onContentChanged()
        viewPager = findViewById(R.id.view_pager) as ViewPager
    }

    override fun onBackPressed() {
        if (viewPager.currentItem != 0) {
            val fragment = adapter.instantiateItem(viewPager, 1) as SkillsFragment?
            fragment!!.applyToSelected(mSelectedSkills)
            viewPager.currentItem = 0
            return
        }
        val data = Intent()
        data.putParcelableArrayListExtra(Constants.EXTRA_SKILLS, mSelectedSkills)
        setResult(Activity.RESULT_OK, data)
        super.onBackPressed()
    }

    class CategoriesFragment : ListFragment(), LoaderManager.LoaderCallbacks<TaskResponse<List<SkillCategory>>> {
        private var adapter: CategoriesAdapter? = null

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            loaderManager.initLoader(0, null, this)
            adapter = CategoriesAdapter(activity)
            listAdapter = adapter
            setListShownNoAnimation(false)
        }

        override fun onCreateLoader(id: Int, args: Bundle): Loader<TaskResponse<List<SkillCategory>>> {
            return SkillCategoriesLoader(activity, Utils.getCurrentAccount(activity)!!, false, false)
        }

        override fun onLoadFinished(loader: Loader<TaskResponse<List<SkillCategory>>>, data: TaskResponse<List<SkillCategory>>) {
            adapter!!.clear()
            if (data.hasData()) {
                adapter!!.addAll(data.data)
            }
            setListShown(true)
        }

        override fun onLoaderReset(loader: Loader<TaskResponse<List<SkillCategory>>>) {
            adapter!!.clear()
        }

        override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
            super.onListItemClick(l, v, position, id)
            val activity = activity as SkillSelectorActivity
            activity.selectedCategory = adapter!!.getItem(position)
            activity.showSkills()
        }

        private inner class CategoriesAdapter(context: Context) : ArrayAdapter<SkillCategory>(context, android.R.layout.simple_list_item_1) {

            override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val text1 = view.findViewById(android.R.id.text1) as TextView
                text1.text = getItem(position).nameString
                return view
            }
        }
    }

    private val selectedSkills: List<Skill>
        get() = mSelectedSkills

    private fun showSkills() {
        viewPager.currentItem = 1
    }

    class SkillsFragment : ListFragment() {

        private var mSkillsAdapter: SkillsAdapter? = null

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            mSkillsAdapter = SkillsAdapter(activity)
            listAdapter = mSkillsAdapter
            listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        }

        override fun setUserVisibleHint(isVisibleToUser: Boolean) {
            super.setUserVisibleHint(isVisibleToUser)
            if (isVisibleToUser) {
                val listView = listView
                listView.clearChoices()
                val activity = activity as SkillSelectorActivity
                val selected = activity.selectedSkills
                val skillCategory = activity.selectedCategory
                mSkillsAdapter!!.clear()
                if (skillCategory != null) {
                    val skills = skillCategory.skills
                    mSkillsAdapter!!.addAll(skills)
                    var i = 0
                    val j = listView.count
                    while (i < j) {
                        listView.setItemChecked(i, isSelected(selected, mSkillsAdapter!!.getItem(i).id))
                        i++
                    }
                }
            }

        }

        override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
            super.onListItemClick(l, v, position, id)
        }

        private fun isSelected(skills: List<Skill>?, id: String?): Boolean {
            if (skills == null || id == null) return false
            for (skill in skills) {
                if (id == skill.id) return true
            }
            return false
        }

        fun applyToSelected(selectedSkills: MutableList<Skill>) {
            val listView = listView
            var i = 0
            val j = listView.count
            while (i < j) {
                val checked = listView.isItemChecked(i)
                val current = mSkillsAdapter!!.getItem(i)
                val inList = Utils.findSkill(selectedSkills, current.id)
                if (checked && inList == null) {
                    selectedSkills.add(current)
                } else if (!checked && inList != null) {
                    selectedSkills.remove(inList)
                }
                i++
            }
        }

        private inner class SkillsAdapter(context: Context) : ArrayAdapter<Skill>(context, android.R.layout.simple_list_item_multiple_choice) {

            override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val text1 = view.findViewById(android.R.id.text1) as TextView
                text1.text = getItem(position).nameString
                return view
            }
        }
    }
}
