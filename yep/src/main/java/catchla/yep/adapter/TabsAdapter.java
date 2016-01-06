package catchla.yep.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import catchla.yep.model.TabSpec;
import catchla.yep.view.iface.PagerIndicator;

/**
 * Created by mariotaku on 15/5/21.
 */
public class TabsAdapter extends SupportFixedFragmentStatePagerAdapter implements PagerIndicator.TabProvider, PagerIndicator.TabListener {
    private final Context mContext;
    private Fragment mPrimaryItem;
    private PagerIndicator.TabListener mTabListener;

    public TabsAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    private List<TabSpec> mTabs = new ArrayList<>();

    @Override
    public Fragment getItem(int position) {
        final TabSpec spec = mTabs.get(position);
        return Fragment.instantiate(mContext, spec.cls.getName(), spec.args);
    }

    public void addTab(Class<? extends Fragment> cls, CharSequence title, int icon, Bundle args) {
        mTabs.add(new TabSpec(cls, title, icon, args));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public Drawable getPageIcon(int position) {
        final TabSpec spec = mTabs.get(position);
        if (spec.icon == 0) return null;
        return ContextCompat.getDrawable(mContext, spec.icon);
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        final TabSpec spec = mTabs.get(position);
        return spec.title;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mPrimaryItem = (Fragment) object;
    }

    public void setTabListener(PagerIndicator.TabListener listener) {
        mTabListener = listener;
    }

    public Fragment getPrimaryItem() {
        return mPrimaryItem;
    }

    @Override
    public void onPageReselected(final int position) {
        if (mTabListener == null) return;
        mTabListener.onPageReselected(position);
    }

    @Override
    public boolean onTabLongClick(final int position) {
        if (mTabListener == null) return false;
        return mTabListener.onTabLongClick(position);
    }

    @Override
    public void onPageSelected(final int position) {
        if (mTabListener == null) return;
        mTabListener.onPageSelected(position);
    }
}
