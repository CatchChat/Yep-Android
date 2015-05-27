/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import catchla.yep.Constants;
import catchla.yep.loader.DiscoverLoader;
import catchla.yep.model.DiscoverQuery;
import catchla.yep.model.PagedUsers;
import catchla.yep.model.TaskResponse;
import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 15/4/29.
 */
public class DiscoverFragment extends Fragment implements Constants,
        LoaderManager.LoaderCallbacks<TaskResponse<PagedUsers>> {


    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<TaskResponse<PagedUsers>> onCreateLoader(final int id, final Bundle args) {
        final DiscoverQuery.Builder builder = new DiscoverQuery.Builder();
        return new DiscoverLoader(getActivity(), Utils.getCurrentAccount(getActivity()), builder.build());
    }

    @Override
    public void onLoadFinished(final Loader<TaskResponse<PagedUsers>> loader, final TaskResponse<PagedUsers> data) {
        Log.d(LOGTAG, data.toString());
    }

    @Override
    public void onLoaderReset(final Loader<TaskResponse<PagedUsers>> loader) {

    }
}
