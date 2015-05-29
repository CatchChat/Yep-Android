/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.model.User;
import catchla.yep.util.MathUtils;
import catchla.yep.util.Utils;
import catchla.yep.view.HeaderDrawerLayout;
import catchla.yep.view.UserHeaderSpaceLayout;
import catchla.yep.view.iface.IExtendedView;

/**
 * Created by mariotaku on 15/4/29.
 */
public class UserFragment extends Fragment implements Constants,
        HeaderDrawerLayout.DrawerCallback, IExtendedView.OnFitSystemWindowsListener {
    private HeaderDrawerLayout mHeaderDrawerLayout;
    private ScrollView mScrollView;
    private ImageView mProfileImageView;
    private UserHeaderSpaceLayout mHeaderSpaceLayout;
    private TextView mIntroductionView;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHeaderDrawerLayout.setDrawerCallback(this);

        final Bundle args = getArguments();
        final User user;
        if (args != null && args.containsKey(EXTRA_USER)) {
            try {
                user = LoganSquare.parse(args.getString(EXTRA_USER), User.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            user = Utils.getCurrentAccountUser(getActivity());
        }
        if (user != null) {
            Picasso.with(getActivity()).load(user.getAvatarUrl()).into(mProfileImageView);
            final String introduction = user.getIntroduction();
            if (TextUtils.isEmpty(introduction)) {
                mIntroductionView.setText(R.string.no_introduction_yet);
            } else {
                mIntroductionView.setText(introduction);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHeaderDrawerLayout = (HeaderDrawerLayout) view.findViewById(R.id.header_drawer);
        mScrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        mProfileImageView = (ImageView) view.findViewById(R.id.profile_image);
        mHeaderSpaceLayout = ((UserHeaderSpaceLayout) view.findViewById(R.id.header_space));
        mIntroductionView = (TextView) view.findViewById(R.id.introduction);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public boolean canScroll(float dy) {
        return ViewCompat.canScrollVertically(mScrollView, (int) dy);
    }

    @Override
    public void cancelTouch() {
        mScrollView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
    }

    @Override
    public void fling(float velocity) {
        mScrollView.fling((int) velocity);
    }

    @Override
    public boolean isScrollContent(float x, float y) {
        final ScrollView v = mScrollView;
        final int[] location = new int[2];
        v.getLocationInWindow(location);
        return x >= location[0] && x <= location[0] + v.getWidth()
                && y >= location[1] && y <= location[1] + v.getHeight();
    }

    @Override
    public void scrollBy(float dy) {
        mScrollView.scrollBy(0, (int) dy);
    }

    @Override
    public boolean shouldLayoutHeaderBottom() {
        return true;
    }

    @Override
    public void topChanged(int offset) {
        mProfileImageView.setTranslationY(MathUtils.clamp(offset, 0, -mProfileImageView.getHeight()) * 0.3f);
        final FragmentActivity activity = getActivity();
        if (activity instanceof HeaderDrawerLayout.DrawerCallback) {
            ((HeaderDrawerLayout.DrawerCallback) activity).topChanged(offset);
        }
    }

    @Override
    public void onFitSystemWindows(Rect insets) {
        mHeaderDrawerLayout.setPadding(insets.left, insets.top, insets.right, insets.bottom);
        mHeaderSpaceLayout.setMinusTop(insets.top);
    }

    public int getHeaderSpaceHeight() {
        return mHeaderSpaceLayout.getMeasuredHeight();
    }

    public int getHeaderPaddingTop() {
        if (mHeaderDrawerLayout == null) return 0;
        return mHeaderDrawerLayout.getPaddingTop();
    }
}
