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
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import org.apache.commons.lang3.ArrayUtils;
import org.mariotaku.mediaviewer.library.AbsMediaViewerActivity;
import org.mariotaku.mediaviewer.library.FileCache;
import org.mariotaku.mediaviewer.library.MediaDownloader;
import org.mariotaku.mediaviewer.library.MediaViewerFragment;
import org.mariotaku.mediaviewer.library.subsampleimageview.SubsampleImageViewerFragment;

import javax.inject.Inject;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.model.Attachment;
import catchla.yep.model.FileAttachment;
import catchla.yep.util.dagger.GeneralComponentHelper;

public final class MediaViewerActivity extends AbsMediaViewerActivity implements Constants, OnPageChangeListener {

    @Inject
    FileCache mFileCache;
    @Inject
    MediaDownloader mMediaDownloader;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        GeneralComponentHelper.build(this).inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getInitialPosition() {
        final Intent intent = getIntent();
        return ArrayUtils.indexOf(getAttachments(), intent.getParcelableExtra(EXTRA_CURRENT_MEDIA));
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_media_viewer;
    }

    @Override
    public ViewPager findViewPager() {
        return (ViewPager) findViewById(R.id.view_pager);
    }

    @Override
    public boolean isBarShowing() {
        return false;
    }

    @Override
    public void setBarVisibility(final boolean visible) {

    }

    @Override
    public MediaDownloader getDownloader() {
        return mMediaDownloader;
    }

    @Override
    public FileCache getFileCache() {
        return mFileCache;
    }

    @Override
    public MediaViewerFragment instantiateMediaFragment(final int position) {
        final Attachment attachment = (Attachment) getAttachments()[position];
        return SubsampleImageViewerFragment.get(Uri.parse(((FileAttachment) attachment).getFile().getUrl()));
    }

    Parcelable[] getAttachments() {
        return getIntent().getParcelableArrayExtra(EXTRA_MEDIA);
    }

    @Override
    public int getMediaCount() {
        return getAttachments().length;
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(final int position) {

    }

    @Override
    public void onPageScrollStateChanged(final int state) {

    }
}
