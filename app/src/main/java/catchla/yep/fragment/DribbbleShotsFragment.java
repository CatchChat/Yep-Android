package catchla.yep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.List;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.loader.DribbbleShotsLoader;
import catchla.yep.model.DribbbleShot;
import catchla.yep.model.DribbbleShots;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.User;
import catchla.yep.util.Utils;
import catchla.yep.view.holder.DribbbleShotViewHolder;

/**
 * Created by mariotaku on 15/6/3.
 */
public class DribbbleShotsFragment extends Fragment implements Constants,
        LoaderManager.LoaderCallbacks<TaskResponse<DribbbleShots>> {

    private RecyclerView mRecyclerView;
    private ShotsAdapter mAdapter;
    private View mLoadProgress;

    @Override
    public Loader<TaskResponse<DribbbleShots>> onCreateLoader(final int id, final Bundle args) {
        final String userId;
        try {
            final Bundle fragmentArgs = getArguments();
            final User user = LoganSquare.parse(fragmentArgs.getString(EXTRA_USER), User.class);
            userId = user.getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new DribbbleShotsLoader(getActivity(), Utils.getCurrentAccount(getActivity()), userId,
                false, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ShotsAdapter(getActivity());
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
        showProgress();
    }

    @Override
    public void onLoadFinished(final Loader<TaskResponse<DribbbleShots>> loader, final TaskResponse<DribbbleShots> data) {
        if (data.hasData()) {
            mAdapter.setData(data.getData().getShots());
        } else {
            mAdapter.setData(null);
        }
        showContent();
    }

    @Override
    public void onLoaderReset(final Loader<TaskResponse<DribbbleShots>> loader) {
        mAdapter.setData(null);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLoadProgress = view.findViewById(R.id.load_progress);
    }

    private void showContent() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadProgress.setVisibility(View.GONE);
    }

    private void showProgress() {
        mRecyclerView.setVisibility(View.GONE);
        mLoadProgress.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dribbble_shots, container, false);
    }

    private static class ShotsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final LayoutInflater mLayoutInflater;
        private List<DribbbleShot> mShots;

        public ShotsAdapter(final Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            return new DribbbleShotViewHolder(mLayoutInflater.inflate(R.layout.grid_item_dribbble_shot, parent, false));
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            ((DribbbleShotViewHolder) holder).displayShot(mShots.get(position));
        }

        @Override
        public int getItemCount() {
            if (mShots == null) return 0;
            return mShots.size();
        }

        public void setData(List<DribbbleShot> shots) {
            mShots = shots;
            notifyDataSetChanged();
        }
    }
}
