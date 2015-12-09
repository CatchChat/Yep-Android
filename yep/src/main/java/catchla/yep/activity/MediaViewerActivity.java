/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package catchla.yep.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnGenericMotionListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.adapter.SupportFixedFragmentStatePagerAdapter;
import catchla.yep.fragment.BaseFragment;
import catchla.yep.loader.TileImageLoader;
import catchla.yep.model.Attachment;
import catchla.yep.model.FileAttachment;
import catchla.yep.util.MenuUtils;
import catchla.yep.util.Utils;
import pl.droidsonroids.gif.GifSupportChecker;
import pl.droidsonroids.gif.GifTextureView;
import pl.droidsonroids.gif.InputSource;


public final class MediaViewerActivity extends ContentActivity implements Constants, OnPageChangeListener {

    private static boolean ANIMATED_GIF_SUPPORTED = GifSupportChecker.isSupported();
    private ViewPager mViewPager;
    private MediaPagerAdapter mPagerAdapter;


    @Override
    protected boolean isTintBarEnabled() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_media_viewer);
        mPagerAdapter = new MediaPagerAdapter(this);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.element_spacing_normal));
        mViewPager.addOnPageChangeListener(this);
        final Intent intent = getIntent();
        final Attachment[] media = Utils.newParcelableArray(intent.getParcelableArrayExtra(EXTRA_MEDIA), Attachment.CREATOR);
        final Attachment currentMedia = intent.getParcelableExtra(EXTRA_CURRENT_MEDIA);
        mPagerAdapter.setMedia(media);
        final int currentIndex = ArrayUtils.indexOf(media, currentMedia);
        if (currentIndex != -1) {
            mViewPager.setCurrentItem(currentIndex, false);
        }
        updatePositionTitle();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        updatePositionTitle();
        setBarVisibility(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private boolean isBarShowing() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return false;
        return actionBar.isShowing();
    }

    private void setBarVisibility(boolean visible) {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        if (visible) {
            actionBar.show();
        } else {
            actionBar.hide();
        }

    }

    private void toggleBar() {
        setBarVisibility(!isBarShowing());
    }

    private void updatePositionTitle() {
        setTitle(String.format("%d / %d", mViewPager.getCurrentItem() + 1, mPagerAdapter.getCount()));
    }

    public static class BaseImagePageFragment extends AbsMediaPageFragment
            implements TileImageLoader.DownloadListener, LoaderCallbacks<TileImageLoader.Result>, OnClickListener {

        private SubsamplingScaleImageView mImageView;
        private ProgressWheel mProgressBar;
        private boolean mLoaderInitialized;
        private float mContentLength;

        private File mImageFile;

        @Override
        public void onBaseViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onBaseViewCreated(view, savedInstanceState);
            mImageView = (SubsamplingScaleImageView) view.findViewById(R.id.image_view);
            mProgressBar = (ProgressWheel) view.findViewById(R.id.load_progress);
        }

        @Override
        public void onClick(View v) {
            final MediaViewerActivity activity = (MediaViewerActivity) getActivity();
            if (activity == null) return;
            activity.toggleBar();
        }

        @Override
        public Loader<TileImageLoader.Result> onCreateLoader(final int id, final Bundle args) {
            setLoadProgressVisibility(View.VISIBLE);
            mProgressBar.spin();
            invalidateOptionsMenu();
            final FileAttachment media = (FileAttachment) getMedia();
            return new TileImageLoader(getActivity(), this, Uri.parse(media.getFile().getUrl()));
        }

        @Override
        public void onLoadFinished(final Loader<TileImageLoader.Result> loader, final TileImageLoader.Result data) {
            if (data.hasData()) {
                mImageFile = data.file;
                if (data.useDecoder) {
                    setImageViewVisibility(View.VISIBLE);
                    mImageView.setImage(ImageSource.uri(Uri.fromFile(data.file)));
                } else {
                    setImageViewVisibility(View.VISIBLE);
                    mImageView.setImage(ImageSource.bitmap(data.bitmap));
                }
            } else {
                mImageView.recycle();
                mImageFile = null;
                setImageViewVisibility(View.GONE);
                Toast.makeText(getContext(), R.string.unable_to_load_media, Toast.LENGTH_SHORT).show();
            }
            setLoadProgressVisibility(View.GONE);
            setLoadProgress(0);
            invalidateOptionsMenu();
        }

        @Override
        public void onLoaderReset(final Loader<TileImageLoader.Result> loader) {
        }

        @Override
        public void onDownloadError(final Throwable t) {
            mContentLength = 0;
        }

        @Override
        public void onDownloadFinished() {
            mContentLength = 0;
        }

        @Override
        public void onDownloadStart(final long total) {
            mContentLength = total;
            mProgressBar.spin();
        }

        @Override
        public void onProgressUpdate(final long downloaded) {
            if (mContentLength <= 0) {
                if (!mProgressBar.isSpinning()) {
                    mProgressBar.spin();
                }
                return;
            }
            setLoadProgress(downloaded / mContentLength);
        }

        protected void setImageViewVisibility(int visible) {
            mImageView.setVisibility(visible);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_media_page_image_compat, container, false);
        }

        protected void setLoadProgress(float progress) {
            mProgressBar.setProgress(progress);
        }

        protected void setLoadProgressVisibility(int visibility) {
            mProgressBar.setVisibility(visibility);
        }

        private Attachment getMedia() {
            final Bundle args = getArguments();
            return args.getParcelable(EXTRA_MEDIA);
        }

        private void loadImage() {
            getLoaderManager().destroyLoader(0);
            if (!mLoaderInitialized) {
                getLoaderManager().initLoader(0, getArguments(), this);
                mLoaderInitialized = true;
            } else {
                getLoaderManager().restartLoader(0, getArguments(), this);
            }
        }

        @Override
        public void onPrepareOptionsMenu(Menu menu) {
            super.onPrepareOptionsMenu(menu);
            final boolean isLoading = getLoaderManager().hasRunningLoaders();
            final TaskRunnable<File, Pair<Boolean, Intent>, Pair<Fragment, Menu>> checkState = new TaskRunnable<File, Pair<Boolean, Intent>, Pair<Fragment, Menu>>() {
                @Override
                public Pair<Boolean, Intent> doLongOperation(File file) throws InterruptedException {
                    final boolean hasImage = file != null && file.exists();
                    if (!hasImage) {
                        return Pair.create(false, null);
                    }
                    return Pair.create(true, null);
                }

                @Override
                public void callback(Pair<Fragment, Menu> callback, Pair<Boolean, Intent> result) {
                    if (callback.first.isDetached() || callback.first.getActivity() == null) return;
                    final Menu menu = callback.second;
                    final boolean hasImage = result.first;
                    MenuUtils.setMenuItemAvailability(menu, R.id.refresh, !hasImage && !isLoading);
                }
            };
            checkState.setParams(mImageFile);
            checkState.setResultHandler(Pair.<Fragment, Menu>create(this, menu));
            AsyncManager.runBackgroundTask(checkState);
        }


        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_media_viewer_image_page, menu);
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.refresh: {
                    loadImage();
                    return true;
                }
            }
            return super.onOptionsItemSelected(item);
        }


        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setHasOptionsMenu(true);
            mImageView.setOnClickListener(this);
            mImageView.setOnGenericMotionListener(new OnGenericMotionListener() {
                @Override
                public boolean onGenericMotion(View v, MotionEvent event) {
                    final SubsamplingScaleImageView iv = (SubsamplingScaleImageView) v;
                    return false;
                }
            });
            loadImage();
        }


    }

    public static final class ImagePageFragment extends BaseImagePageFragment
            implements TileImageLoader.DownloadListener, LoaderCallbacks<TileImageLoader.Result>, OnClickListener {

        private GifTextureView mGifImageView;

        @Override
        public void onBaseViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onBaseViewCreated(view, savedInstanceState);
            mGifImageView = (GifTextureView) view.findViewById(R.id.gif_image_view);
        }


        @Override
        public void onLoadFinished(final Loader<TileImageLoader.Result> loader, final TileImageLoader.Result data) {
            if (data.hasData() && "image/gif".equals(data.options.outMimeType)) {
                mGifImageView.setVisibility(View.VISIBLE);
                setImageViewVisibility(View.GONE);
                mGifImageView.setInputSource(new InputSource.FileSource(data.file));
                setLoadProgressVisibility(View.GONE);
                setLoadProgress(0);
                invalidateOptionsMenu();
                return;
            }
            super.onLoadFinished(loader, data);
        }


        @Override
        public void onLoaderReset(final Loader<TileImageLoader.Result> loader) {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_media_page_image, container, false);
        }


    }

    private static class MediaPagerAdapter extends SupportFixedFragmentStatePagerAdapter {

        private final MediaViewerActivity mActivity;
        private Attachment[] mMedia;

        public MediaPagerAdapter(MediaViewerActivity activity) {
            super(activity.getSupportFragmentManager());
            mActivity = activity;
        }

        @Override
        public int getCount() {
            if (mMedia == null) return 0;
            return mMedia.length;
        }

        @Override
        public Fragment getItem(int position) {
            final Attachment media = mMedia[position];
            final Bundle args = new Bundle();
            args.putParcelable(EXTRA_MEDIA, media);
            if ("image".equals(media.getKind())) {
                if (ANIMATED_GIF_SUPPORTED) {
                    return Fragment.instantiate(mActivity, ImagePageFragment.class.getName(), args);
                }
                return Fragment.instantiate(mActivity, BaseImagePageFragment.class.getName(), args);
            }
            throw new UnsupportedOperationException("Unsupported attachment kind  " + media.getKind());
        }

        public void setMedia(Attachment[] media) {
            mMedia = media;
            notifyDataSetChanged();
        }
    }

    private static abstract class AbsMediaPageFragment extends BaseFragment {

    }

}
