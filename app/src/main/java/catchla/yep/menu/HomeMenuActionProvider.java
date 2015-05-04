/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.menu;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;

import catchla.yep.R;
import catchla.yep.adapter.ArrayAdapter;
import catchla.yep.util.ThemeUtils;

/**
 * Created by mariotaku on 15/5/4.
 */
public class HomeMenuActionProvider extends ActionProvider implements View.OnClickListener {

    private final int mPopupMaxWidth;
    private View mActionView;
    private ListPopupWindow mOverflowPopup;
    private HomeMenuActionsAdapter mActionsAdapter;
    private MergeAdapter mAdapter;
    private boolean mHasContentWidth;
    private int mContentWidth;
    private Runnable mPostedOpenRunnable;

    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public HomeMenuActionProvider(Context context) {
        super(context);
        final Resources res = context.getResources();
        mPopupMaxWidth = Math.max(res.getDisplayMetrics().widthPixels / 2,
                res.getDimensionPixelSize(android.support.v7.appcompat.R.dimen.abc_panel_menu_list_width));
    }

    @Override
    public boolean onPerformDefaultAction() {
        return super.onPerformDefaultAction();
    }

    @Override
    public View onCreateActionView() {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_action_item_home_menu, null);
        view.setOnClickListener(this);
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                dismissPopup();
            }
        });
        view.setOnTouchListener(new ListPopupWindow.ForwardingListener(view) {
            @Override
            public ListPopupWindow getPopup() {
                return mOverflowPopup;
            }

            @Override
            public boolean onForwardingStarted() {
                showPopup();
                return true;
            }

            @Override
            public boolean onForwardingStopped() {
                // Displaying the popup occurs asynchronously, so wait for
                // the runnable to finish before deciding whether to stop
                // forwarding.
                if (mPostedOpenRunnable != null) {
                    return false;
                }

                dismissPopup();
                return true;
            }
        });

        final Context popupContext = ThemeUtils.getActionBarPopupThemedContext(getContext());

        mAdapter = new MergeAdapter();
        //noinspection Annotator
        mAdapter.addView(LayoutInflater.from(popupContext).inflate(R.layout.header_home_menu_profile, null), true);
        mAdapter.addView(LayoutInflater.from(popupContext).inflate(R.layout.layout_divider_vertical, null), false);
        mAdapter.addAdapter(mActionsAdapter = new HomeMenuActionsAdapter(popupContext));

        mOverflowPopup = new ListPopupWindow(popupContext, null, android.support.v7.appcompat.R.attr.actionOverflowMenuStyle, 0);
        mOverflowPopup.setModal(true);
        mOverflowPopup.setAdapter(mAdapter);
        mOverflowPopup.setAnchorView(view);
        mOverflowPopup.setDropDownGravity(GravityCompat.END);
        mOverflowPopup.setHorizontalOffset(-popupContext.getResources().getDimensionPixelOffset(R.dimen.element_spacing_normal));
        mOverflowPopup.setVerticalOffset(popupContext.getResources().getDimensionPixelOffset(R.dimen.element_spacing_small));


        if (!mHasContentWidth) {
            mContentWidth = measureContentWidth();
            mHasContentWidth = true;
        }


        mOverflowPopup.setContentWidth(mContentWidth);
        mOverflowPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        mActionView = view;
        return view;
    }

    private void showPopup() {
        if (mActionView == null || mOverflowPopup == null || mOverflowPopup.isShowing()) return;
        mPostedOpenRunnable = new ShowPopupRunnable(mOverflowPopup);
        mActionView.post(mPostedOpenRunnable);
    }

    @Override
    public boolean hasSubMenu() {
        return false;
    }

    @Override
    public void onClick(View v) {
        dismissPopup();

        mActionsAdapter.clear();
        mActionsAdapter.add("Item 1");
        mActionsAdapter.add("Item 2");
        mActionsAdapter.add("Item 3");

//        mOverflowPopup.getListView().setOnKeyListener(this);
        showPopup();
    }

    private void dismissPopup() {
        if (mOverflowPopup == null || !mOverflowPopup.isShowing()) return;
        mOverflowPopup.dismiss();
    }

    private static class HomeMenuActionsAdapter extends ArrayAdapter<String> {

        public HomeMenuActionsAdapter(Context context) {
            super(context, R.layout.list_item_menu);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = super.getView(position, convertView, parent);
            final ImageView icon = ((ImageView) view.findViewById(android.R.id.icon));
            icon.setVisibility(View.GONE);
            final TextView text1 = (TextView) view.findViewById(android.R.id.text1);
            text1.setText(getItem(position));
            return view;
        }
    }

    private ViewGroup mMeasureParent;

    private int measureContentWidth() {
        // Menus don't tend to be long, so this is more sane than it looks.
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        final ListAdapter adapter = mAdapter;
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(getContext());
            }

            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemWidth = itemView.getMeasuredWidth();
            if (itemWidth >= mPopupMaxWidth) {
                return mPopupMaxWidth;
            } else if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }

        return maxWidth;
    }


    private class ShowPopupRunnable implements Runnable {
        private ListPopupWindow mPopup;

        public ShowPopupRunnable(ListPopupWindow popup) {
            mPopup = popup;
        }

        public void run() {
            final View itemView = mActionView;
            if (itemView != null && itemView.getWindowToken() != null) {
                mPopup.show();
            }
            mPostedOpenRunnable = null;
        }
    }
}
