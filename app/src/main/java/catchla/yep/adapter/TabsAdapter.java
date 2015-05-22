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
public class TabsAdapter extends FragmentStatePagerAdapter implements PagerIndicator.TabProvider {
    private final Context mContext;
    private Fragment mPrimaryItem;

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
        return ContextCompat.getDrawable(mContext, mTabs.get(position).icon);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mPrimaryItem = (Fragment) object;
    }

    public Fragment getPrimaryItem() {
        return mPrimaryItem;
    }
}
