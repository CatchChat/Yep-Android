package catchla.yep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mariotaku on 15/8/31.
 */
public class LayoutAdapter extends BaseAdapter {

    private List<LayoutItem> mItems = new ArrayList<>();

    private final LayoutInflater mInflater;
    private final Context mContext;

    public LayoutAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public void add(int layoutRes, String tag, boolean enabled) {
        mItems.add(new LayoutItem(layoutRes, tag, enabled));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public LayoutItem getItem(final int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return System.identityHashCode(getItem(position));
    }

    @Override
    public boolean isEnabled(final int position) {
        return getItem(position).enabled;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getItemViewType(final int position) {
        return position + 1;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final View view;
        final LayoutItem item = getItem(position);
        if (convertView != null) {
            view = convertView;
        } else {
            view = mInflater.inflate(item.layoutRes, parent, false);
        }
        bindView(view, position, item.tag);
        return view;
    }

    protected void bindView(final View view, final int position, final String tag) {
    }

    public Context getContext() {
        return mContext;
    }

    public final static class LayoutItem {
        int layoutRes;
        String tag;
        boolean enabled;

        private LayoutItem(final int layoutRes, final String tag, final boolean enabled) {
            this.layoutRes = layoutRes;
            this.tag = tag;
            this.enabled = enabled;
        }

    }
}
