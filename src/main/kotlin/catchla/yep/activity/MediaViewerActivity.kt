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

package catchla.yep.activity

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.model.Attachment
import catchla.yep.model.FileAttachment
import catchla.yep.util.dagger.GeneralComponentHelper
import org.apache.commons.lang3.ArrayUtils
import org.mariotaku.mediaviewer.library.AbsMediaViewerActivity
import org.mariotaku.mediaviewer.library.FileCache
import org.mariotaku.mediaviewer.library.MediaDownloader
import org.mariotaku.mediaviewer.library.MediaViewerFragment
import org.mariotaku.mediaviewer.library.subsampleimageview.SubsampleImageViewerFragment
import javax.inject.Inject

class MediaViewerActivity : AbsMediaViewerActivity(), Constants, OnPageChangeListener {

    @Inject
    lateinit var mFileCache: FileCache
    @Inject
    lateinit var mMediaDownloader: MediaDownloader

    override fun onCreate(savedInstanceState: Bundle?) {
        GeneralComponentHelper.build(this).inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun getInitialPosition(): Int {
        val intent = intent
        return ArrayUtils.indexOf(attachments, intent.getParcelableExtra<Parcelable>(Constants.EXTRA_CURRENT_MEDIA))
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_media_viewer
    }

    override fun findViewPager(): ViewPager {
        return findViewById(R.id.view_pager) as ViewPager
    }

    override fun isBarShowing(): Boolean {
        return false
    }

    override fun setBarVisibility(visible: Boolean) {

    }

    override fun getDownloader(): MediaDownloader {
        return mMediaDownloader
    }

    override fun getFileCache(): FileCache {
        return mFileCache
    }

    override fun instantiateMediaFragment(position: Int): MediaViewerFragment {
        val attachment = attachments[position] as Attachment
        return SubsampleImageViewerFragment.get(Uri.parse((attachment as FileAttachment).file.url))
    }

    internal val attachments: Array<Parcelable>
        get() = intent.getParcelableArrayExtra(Constants.EXTRA_MEDIA)

    override fun getMediaCount(): Int {
        return attachments.size
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }
}
