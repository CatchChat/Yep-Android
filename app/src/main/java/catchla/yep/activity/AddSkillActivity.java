package catchla.yep.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import catchla.yep.R;
import catchla.yep.adapter.TabsAdapter;
import catchla.yep.loader.SkillCategoriesLoader;
import catchla.yep.model.Skill;
import catchla.yep.model.SkillCategory;
import catchla.yep.model.TaskResponse;
import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 15/6/10.
 */
public class AddSkillActivity extends ContentActivity {

    private ViewPager mViewPager;
    private TabsAdapter mAdapter;
    private SkillCategory selectedCategory;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_skill);
        mAdapter = new TabsAdapter(this, getSupportFragmentManager());
        mViewPager.setEnabled(false);
        mViewPager.setAdapter(mAdapter);

        mAdapter.addTab(CategoriesFragment.class, null, 0, null);
        mAdapter.addTab(SkillsFragment.class, null, 0, null);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
    }

    public void setSelectedCategory(final SkillCategory selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public SkillCategory getSelectedCategory() {
        return selectedCategory;
    }


    public static class CategoriesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<TaskResponse<List<SkillCategory>>> {
        private CategoriesAdapter mCategoriesAdapter;

        @Override
        public void onActivityCreated(final Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getLoaderManager().initLoader(0, null, this);
            setListAdapter(mCategoriesAdapter = new CategoriesAdapter(getActivity()));
            setListShownNoAnimation(false);
        }

        @Override
        public Loader<TaskResponse<List<SkillCategory>>> onCreateLoader(final int id, final Bundle args) {
            return new SkillCategoriesLoader(getActivity(), Utils.getCurrentAccount(getActivity()), false, false);
        }

        @Override
        public void onLoadFinished(final Loader<TaskResponse<List<SkillCategory>>> loader, final TaskResponse<List<SkillCategory>> data) {
            mCategoriesAdapter.clear();
            if (data.hasData()) {
                mCategoriesAdapter.addAll(data.getData());
            }
            setListShown(true);
        }

        @Override
        public void onLoaderReset(final Loader<TaskResponse<List<SkillCategory>>> loader) {
            mCategoriesAdapter.clear();
        }

        @Override
        public void onListItemClick(final ListView l, final View v, final int position, final long id) {
            super.onListItemClick(l, v, position, id);
            final AddSkillActivity activity = (AddSkillActivity) getActivity();
            activity.setSelectedCategory(mCategoriesAdapter.getItem(position));
            activity.showSkills();
        }

        private class CategoriesAdapter extends ArrayAdapter<SkillCategory> {
            public CategoriesAdapter(final Context context) {
                super(context, android.R.layout.simple_list_item_1);
            }

            @Override
            public View getView(final int position, final View convertView, final ViewGroup parent) {
                final View view = super.getView(position, convertView, parent);
                final TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setText(getItem(position).getNameString());
                return view;
            }
        }
    }

    private void showSkills() {
        mViewPager.setCurrentItem(1);
    }

    public static class SkillsFragment extends ListFragment {

        private SkillsAdapter mSkillsAdapter;

        @Override
        public void onActivityCreated(final Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setListAdapter(mSkillsAdapter = new SkillsAdapter(getActivity()));
        }

        @Override
        public void setUserVisibleHint(final boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser) {
                final AddSkillActivity activity = (AddSkillActivity) getActivity();
                final SkillCategory skillCategory = activity.getSelectedCategory();
                mSkillsAdapter.clear();
                if (skillCategory != null) {
                    mSkillsAdapter.addAll(skillCategory.getSkills());
                }
            }
        }

        private class SkillsAdapter extends ArrayAdapter<Skill> {
            public SkillsAdapter(final Context context) {
                super(context, android.R.layout.simple_list_item_1);
            }

            @Override
            public View getView(final int position, final View convertView, final ViewGroup parent) {
                final View view = super.getView(position, convertView, parent);
                final TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setText(getItem(position).getNameString());
                return view;
            }
        }
    }
}
