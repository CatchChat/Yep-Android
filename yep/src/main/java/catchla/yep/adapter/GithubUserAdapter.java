package catchla.yep.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import catchla.yep.R;
import catchla.yep.model.GithubRepo;
import catchla.yep.model.GithubUserInfo;
import catchla.yep.view.holder.GithubRepoItemViewHolder;
import catchla.yep.view.holder.GithubUserHeaderViewHolder;

/**
 * Created by mariotaku on 15/6/4.
 */
public class GithubUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    private final Fragment mFragment;
    private final LayoutInflater mInflater;

    private GithubUserInfo mData;

    public GithubUserAdapter(Fragment fragment, Context context) {
        mFragment = fragment;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            final View view = mInflater.inflate(R.layout.header_github_user, parent, false);
            return new GithubUserHeaderViewHolder(view);
        }
        final View view = mInflater.inflate(R.layout.list_item_github_repo, parent, false);
        return new GithubRepoItemViewHolder(mFragment, view, this);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (position == 0) {
            ((GithubUserHeaderViewHolder) holder).displayUser(mData);
        } else {
            ((GithubRepoItemViewHolder) holder).displayRepo(mData.getRepos().get(position - 1));
        }
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == 0) return VIEW_TYPE_HEADER;
        return VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;
        return mData.getRepos().size() + 1;
    }

    public void setData(GithubUserInfo data) {
        mData = data;
        notifyDataSetChanged();
    }

    public GithubRepo getRepoAt(final int layoutPosition) {
        if (mData == null) return null;
        return mData.getRepos().get(layoutPosition - 1);
    }
}
