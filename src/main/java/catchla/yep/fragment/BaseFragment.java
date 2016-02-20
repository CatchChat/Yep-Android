/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import catchla.yep.Constants;
import catchla.yep.fragment.iface.IBaseFragment;
import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.dagger.GeneralComponentHelper;


public class BaseFragment extends Fragment implements IBaseFragment, Constants {

    @Inject
    protected Bus mBus;
    @Inject
    protected ImageLoaderWrapper mImageLoader;
    @Inject
    protected SharedPreferences mPreferences;

    @Override
    public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onBaseViewCreated(view, savedInstanceState);
        requestFitSystemWindows();
    }

    public BaseFragment() {

    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        GeneralComponentHelper.build(context).inject(this);
    }

    @Override
    public void requestFitSystemWindows() {
        final Activity activity = getActivity();
        final Fragment parentFragment = getParentFragment();
        final SystemWindowsInsetsCallback callback;
        if (parentFragment instanceof SystemWindowsInsetsCallback) {
            callback = (SystemWindowsInsetsCallback) parentFragment;
        } else if (activity instanceof SystemWindowsInsetsCallback) {
            callback = (SystemWindowsInsetsCallback) activity;
        } else {
            return;
        }
        final Rect insets = new Rect();
        if (callback.getSystemWindowsInsets(insets)) {
            fitSystemWindows(insets);
        }
    }

    @Override
    public void onBaseViewCreated(View view, Bundle savedInstanceState) {

    }

    public Context getThemedContext() {
        return getActivity();
    }

    protected void fitSystemWindows(Rect insets) {
        final View view = getView();
        if (view != null) {
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom);
        }
    }
}
