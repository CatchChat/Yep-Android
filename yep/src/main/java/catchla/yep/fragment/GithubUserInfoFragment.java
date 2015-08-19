package catchla.yep.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.adapter.GithubUserAdapter;
import catchla.yep.loader.GithubUserInfoLoader;
import catchla.yep.model.GithubUserInfo;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.User;
import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 15/6/4.
 */
public class GithubUserInfoFragment extends Fragment implements Constants,
        LoaderManager.LoaderCallbacks<TaskResponse<GithubUserInfo>> {


    private RecyclerView mRecyclerView;
    private GithubUserAdapter mAdapter;
    private View mLoadProgress;

    @Override
    public Loader<TaskResponse<GithubUserInfo>> onCreateLoader(final int id, final Bundle args) {
        final String userId;
        try {
            final Bundle fragmentArgs = getArguments();
            final User user = LoganSquare.parse(fragmentArgs.getString(EXTRA_USER), User.class);
            userId = user.getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new GithubUserInfoLoader(getActivity(), Utils.getCurrentAccount(getActivity()), userId,
                false, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new GithubUserAdapter(getActivity());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
        showProgress();
    }

    @Override
    public void onLoadFinished(final Loader<TaskResponse<GithubUserInfo>> loader, final TaskResponse<GithubUserInfo> data) {
        if (data.hasData()) {
            mAdapter.setData(data.getData());
        } else {
            mAdapter.setData(null);
        }
        showContent();
    }

    @Override
    public void onLoaderReset(final Loader<TaskResponse<GithubUserInfo>> loader) {
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
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }
}
