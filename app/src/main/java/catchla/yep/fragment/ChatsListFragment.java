/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import catchla.yep.R;
import catchla.yep.adapter.ChatsListAdapter;
import catchla.yep.adapter.decorator.DividerItemDecoration;

/**
 * Created by mariotaku on 15/4/29.
 */
public class ChatsListFragment extends AbsContentRecyclerViewFragment<ChatsListAdapter> {

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
        showContent();
    }

    @NonNull
    @Override
    protected ChatsListAdapter onCreateAdapter(Context context) {
        return new ChatsListAdapter(context);
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
