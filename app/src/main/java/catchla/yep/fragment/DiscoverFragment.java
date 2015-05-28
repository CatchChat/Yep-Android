/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import catchla.yep.R;
import catchla.yep.adapter.DiscoverAdapter;
import catchla.yep.adapter.FriendsListAdapter;
import catchla.yep.adapter.decorator.DividerItemDecoration;
import catchla.yep.loader.DiscoverLoader;
import catchla.yep.model.DiscoverQuery;
import catchla.yep.model.PagedUsers;
import catchla.yep.model.TaskResponse;
import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 15/4/29.
 */
public class DiscoverFragment extends AbsContentRecyclerViewFragment<DiscoverAdapter>
        implements LoaderManager.LoaderCallbacks<TaskResponse<PagedUsers>> {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        final Context viewContext = getActivity();

        final RecyclerView recyclerView = getRecyclerView();
        final LinearLayoutManager layoutManager = getLayoutManager();
        final DividerItemDecoration itemDecoration = new DividerItemDecoration(viewContext, layoutManager.getOrientation());
        final Resources res = viewContext.getResources();
        final int decorPaddingLeft = res.getDimensionPixelSize(R.dimen.element_spacing_normal) * 2
                + res.getDimensionPixelSize(R.dimen.icon_size_status_profile_image);
        itemDecoration.setPadding(decorPaddingLeft, 0, 0, 0);
        recyclerView.addItemDecoration(itemDecoration);
        getLoaderManager().initLoader(0, null, this);
        showContent();
    }


    @Override
    public Loader<TaskResponse<PagedUsers>> onCreateLoader(final int id, final Bundle args) {
        final DiscoverQuery query = new DiscoverQuery();
        query.masterSkills(new String[]{"ios"});
        return new DiscoverLoader(getActivity(), Utils.getCurrentAccount(getActivity()), query);
    }

    @Override
    public void onLoadFinished(final Loader<TaskResponse<PagedUsers>> loader, final TaskResponse<PagedUsers> data) {
        getAdapter().setData(data.getData());
    }

    @Override
    public void onLoaderReset(final Loader<TaskResponse<PagedUsers>> loader) {

    }

    @NonNull
    @Override
    protected DiscoverAdapter onCreateAdapter(Context context) {
        return new DiscoverAdapter(context);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_chats_list, menu);
    }

    @Override
    public void onBaseViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onBaseViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean isRefreshing() {
        return false;
    }


}
