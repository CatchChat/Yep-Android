package catchla.yep.fragment;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import catchla.yep.activity.UserActivity;
import catchla.yep.adapter.UsersAdapter;
import catchla.yep.adapter.iface.ItemClickListener;
import catchla.yep.loader.SearchUsersLoader;
import catchla.yep.model.User;

/**
 * Created by mariotaku on 15/8/25.
 */
public class SearchUsersFragment extends AbsContentListRecyclerViewFragment<UsersAdapter>
        implements LoaderManager.LoaderCallbacks<List<User>> {

    @Override
    public void onRefresh() {
        super.onRefresh();
        final Bundle loaderArgs = getArguments();
        getLoaderManager().restartLoader(0, loaderArgs, this);
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getAdapter().setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(final int position, final RecyclerView.ViewHolder holder) {
                final User user = getAdapter().getUser(position);
                final Intent intent = new Intent(getActivity(), UserActivity.class);
                intent.putExtra(EXTRA_ACCOUNT, getAccount());
                intent.putExtra(EXTRA_USER, user);
                startActivity(intent);
            }
        });
        final Bundle loaderArgs = getArguments();
        getLoaderManager().initLoader(0, loaderArgs, this);
    }

    private Account getAccount() {
        return getArguments().getParcelable(EXTRA_ACCOUNT);
    }

    @Override
    public boolean isRefreshing() {
        return false;
    }

    @NonNull
    @Override
    protected UsersAdapter onCreateAdapter(Context context) {
        return new UsersAdapter(context);
    }

    @Override
    public Loader<List<User>> onCreateLoader(final int id, final Bundle args) {
        final String query = args.getString(EXTRA_QUERY);
        return new SearchUsersLoader(getActivity(), getAccount(), query);
    }

    @Override
    public void onLoadFinished(final Loader<List<User>> loader, final List<User> data) {
        getAdapter().setData(data);
        showContent();
        setRefreshing(false);
    }

    @Override
    public void onLoaderReset(final Loader<List<User>> loader) {
        getAdapter().setData(null);
    }
}
